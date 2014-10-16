package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.AutoDeleteReservePolicy;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.BackupMethod;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TimeOfDay;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.VmPolicy;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.WeekDays;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class VmPolicyDAOImpl extends CrudDAO<VmPolicy>{

    public VmPolicyDAOImpl() {
        instance = new RowMapper<VmPolicy>() {

            public VmPolicy mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new VmPolicy((UUID) rs.getObject("id"),
                        BackupMethod.forValue(rs.getInt("backup_method")),
                        TimeOfDay.parseTimeOfDay(rs.getString("time_of_day")),
                        WeekDays.parseWeeekDays(rs.getString("week_days")),
                        AutoDeleteReservePolicy.forValue(rs.getInt("auto_delete_reserve_policy")),
                        rs.getInt("auto_delete_reserve_amount"));
            }
        };
        entityName = "VmPolicy";
    }

    public MapSqlParameterSource createFullParametersMapper(VmPolicy vmPolicy) {
        return new MapSqlParameterSource().addValue("id", vmPolicy.getVmID())
                .addValue("backup_method", vmPolicy.getBackupMethod().getValue())
                .addValue("time_of_day", vmPolicy.getTimeOfDay().toString())
                .addValue("week_days", vmPolicy.getWeekDays().toString())
                .addValue("auto_delete_reserve_policy", vmPolicy.getAutoDeleteReservePolicy().getValue())
                .addValue("auto_delete_reserve_amount", vmPolicy.getAutoDeleteReserveAmount());
    }

    public MapSqlParameterSource createIdParametersMapper(UUID id) {
        return new MapSqlParameterSource().addValue("id", id);
    }
}
