package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresJdbcTemplate extends JdbcTemplate {
    private static final String PREFIX = "v_";

    /**
     * @see JdbcTemplate#JdbcTemplate(DataSource)
     */
    public PostgresJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Map extractOutputParameters(CallableStatement cs, List parameters) throws SQLException {
        Map<String, Object> outputParameters = super.extractOutputParameters(cs, parameters);
        Map<String, Object> returnMap = new HashMap<String, Object>(outputParameters.size());

        for (Map.Entry<String, Object> outputEntry : outputParameters.entrySet()) {
            // Get the value before we change the key (otherwise we won't be able to get it later when we need it).
            String parameter = outputEntry.getKey();
            Object value = outputEntry.getValue();
            if (parameter != null && parameter.length() > PREFIX.length() && parameter.startsWith(PREFIX)) {
                parameter = parameter.substring(PREFIX.length());
            }

            returnMap.put(parameter, value);
        }

        return returnMap;
    }
}