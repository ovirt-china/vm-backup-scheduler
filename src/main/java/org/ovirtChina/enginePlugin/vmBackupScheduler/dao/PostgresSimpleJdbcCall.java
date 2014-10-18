package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

public class PostgresSimpleJdbcCall extends SimpleJdbcCall {

    /**
     * This is the key to put in the returned map, emulating the way the {@link SimpleJdbcCallOperations} works.
     */
    private String returnedMapKey;

    /**
     * Row mapper is used to map the Result Set to POJOs.
     */
    private RowMapper rowMapper;

    public PostgresSimpleJdbcCall(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected void compileInternal() {
        // Put a dummy parameter name as an input parameters, otherwise the CallMetaDataContext thinks that the
        // returned column names are parameters.
        Set<String> inParams = new HashSet<String>(getInParameterNames());
        inParams.add("dummyParamNeverUsed");
        setInParameterNames(inParams);
        super.compileInternal();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Map<String, Object> doExecute(SqlParameterSource parameterSource) {
        // Have only the declared parameters participate in the function metadata extraction, otherwise the
        // CallMetaDataContext thinks that the returned column names are parameters.
        getInParameterNames().addAll(
                SqlParameterSourceUtils.extractCaseInsensitiveParameterNames(parameterSource).keySet());
        checkCompiled();
        Map params = matchInParameterValuesWithCallParameters(parameterSource);
        return executeCallInternal(params);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Map<String, Object> doExecute(Map<String, ?> args) {
        // Have only the declared parameters participate in the function metadata extraction, otherwise the
        // CallMetaDataContext thinks that the returned column names are parameters.
        getInParameterNames().addAll(args.keySet());
        checkCompiled();
        Map params = matchInParameterValuesWithCallParameters(args);
        return executeCallInternal(params);
    }

    /**
     * Save the row mapper and parameter name locally to use later in the execution.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public SimpleJdbcCall returningResultSet(String parameterName, RowMapper rowMapper) {
        this.returnedMapKey = parameterName;
        this.rowMapper = rowMapper;
        return this;
    }

    /**
     * Execute the call using a query instead of a procedure call.<br>
     * <br>
     * The way to execute correctly is to use a {@link PreparedStatementSetter} which will set the parameters
     * correctly, since using a PreparedStatementCreator doesn't seem to work well. The setter simply sets the
     * parameter using the correct {@link Types} constant indicating the actual call type.
     */
    private Map<String, Object> executeCallInternal(final Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>(1);
        result.put(returnedMapKey, getJdbcTemplate().query(
                generateSql(),
                        new PreparedStatementSetter() {

                            public void setValues(PreparedStatement ps) throws SQLException {
                                List<SqlParameter> callParameters = getCallParameters();
                                for (int i = 0; i < callParameters.size(); i++) {
                                    SqlParameter parameter = callParameters.get(i);
                                    ps.setObject(i + 1, params.get(parameter.getName()), parameter.getSqlType());
                                }
                            }
                        }, rowMapper));
        return result;
    }

    /**
     * @return The query used for calling the function.
     */
    private String generateSql() {
        StringBuilder builder = new StringBuilder("select * from ");
        builder.append(getCallString().replace("{call", "").replace("}", ""));
        return builder.toString();
    }
}