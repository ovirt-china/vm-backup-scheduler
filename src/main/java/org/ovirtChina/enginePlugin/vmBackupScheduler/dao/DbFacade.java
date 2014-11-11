package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

public class DbFacade {
    private static DbFacade instance;
    private static DataSource ds;
    private static JdbcTemplate template;

    private ConcurrentMap<String, SimpleJdbcCall> callsMap =
            new ConcurrentHashMap<String, SimpleJdbcCall>();
    private TaskDAOImpl taskDAO;
    private VmPolicyDAOImpl vmPolicyDAO;

    public static DataSource locateDataSource() throws NamingException {
        InitialContext cxt = new InitialContext();
        ds = (DataSource) cxt.lookup( "java:/VmBackupSchedulerDataSource" );
        if ( ds == null ) {
           throw new RuntimeException("Data source not found!");
        }
        return ds;
    }

    public <T> List<T> executeReadList(final String procedureName,
            final RowMapper<T> mapper,
            final MapSqlParameterSource parameterSource) {
        return (List<T>) getCall(procedureName, mapper, parameterSource, true)
                .execute(parameterSource).get("RETURN_VALUE");
    }

    public Map<String, Object> executeModification(final String procedureName, final MapSqlParameterSource paramSource) {
        return getCall(procedureName, null, paramSource, false).execute(paramSource);
    }

    protected <T> SimpleJdbcCall getCall(String procedureName, RowMapper<T> mapper,
            MapSqlParameterSource parameterSource, boolean isRead) {
        SimpleJdbcCall call = callsMap.get(procedureName);
        if (call == null) {
            call = isRead ? call =createCallForRead(procedureName, mapper, parameterSource)
                    :createCallForModification(procedureName);
            call.compile();
            callsMap.putIfAbsent(procedureName, call);
        } else if (mapper != null) {
            call.returningResultSet("RETURN_VALUE", mapper);
        }
        return call;
    }

    private SimpleJdbcCall createCallForRead(final String procedureName,
            final RowMapper<?> mapper,
            final MapSqlParameterSource parameterSource) {

                SimpleJdbcCall call =
                        (SimpleJdbcCall) new PostgresSimpleJdbcCall(template).withProcedureName(procedureName);
                call.returningResultSet("RETURN_VALUE", mapper);
                // Pass mapper information (only parameter names) in order to supply all the needed
                // metadata information for compilation.
                call.getInParameterNames().addAll(
                        SqlParameterSourceUtils.extractCaseInsensitiveParameterNames(parameterSource).keySet());
                return call;
    }

    private SimpleJdbcCall createCallForModification(final String procedureName) {
        return new SimpleJdbcCall(ds).withProcedureName(procedureName);
    }

    public static DbFacade getInstance() {
        if (instance == null) {
            instance = new DbFacade();
            template = new PostgresJdbcTemplate(ds);
            instance.setTaskDAO(new TaskDAOImpl());
            instance.setVmPolicyDAO(new VmPolicyDAOImpl());
            return instance;
        }
        return instance;
    }

	public TaskDAOImpl getTaskDAO() {
		return taskDAO;
	}

	public void setTaskDAO(TaskDAOImpl taskDAO) {
		this.taskDAO = taskDAO;
	}

	public VmPolicyDAOImpl getVmPolicyDAO() {
		return vmPolicyDAO;
	}

	public void setVmPolicyDAO(VmPolicyDAOImpl vmPolicyDAO) {
		this.vmPolicyDAO = vmPolicyDAO;
	}
}
