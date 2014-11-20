package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.VmPolicy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class VmPolicyDAOImpl extends CrudDAO<VmPolicy>{

    public List<VmPolicy> getScheduleVms() {
        return DbFacade.getInstance().executeReadList("getScheduleVms", instance, null);
    }

    public VmPolicy get(UUID id, int backupMethod) {
        List<VmPolicy> result =
                (List<VmPolicy>)DbFacade.getInstance().executeReadList(
                        "getVmPolicyByIdBackupMethod", instance,
                        createIdParametersMapper(id).addValue("v_backup_method", backupMethod));
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public VmPolicyDAOImpl() {
        instance = new RowMapper<VmPolicy>() {

            public VmPolicy mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new VmPolicy(((UUID) rs.getObject("id")),
                        rs.getBoolean("enabled"),
                        rs.getInt("backup_method"),
                        rs.getString("time_of_day"),
                        rs.getString("week_days"),
                        rs.getInt("auto_delete_reserve_policy"),
                        rs.getInt("auto_delete_reserve_amount"));
            }
        };
        entityName = "VmPolicy";
    }

    public MapSqlParameterSource createFullParametersMapper(VmPolicy vmPolicy) {
        return new MapSqlParameterSource().addValue("v_id", vmPolicy.getVmID())
                .addValue("v_enabled", vmPolicy.isEnabled())
                .addValue("v_backup_method", vmPolicy.getBackupMethod())
                .addValue("v_time_of_day", vmPolicy.getTimeOfDay().toString())
                .addValue("v_week_days", vmPolicy.getWeekDays().toString())
                .addValue("v_auto_delete_reserve_policy", vmPolicy.getAutoDeleteReservePolicy())
                .addValue("v_auto_delete_reserve_amount", vmPolicy.getAutoDeleteReserveAmount());
    }

    public MapSqlParameterSource createIdParametersMapper(UUID id) {
        return new MapSqlParameterSource().addValue("v_id", id);
    }
}
