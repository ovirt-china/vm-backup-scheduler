package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.ovirtChina.enginePlugin.vmBackupScheduler.common.TaskType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class TaskDAOImpl extends CrudDAO<Task>{

    public TaskDAOImpl() {
        instance = new RowMapper<Task>() {

            public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Task((UUID)rs.getObject("id"),
                    TaskType.forValue(rs.getInt("task_type")),
                    rs.getString("backupName"));
            }
        };
        entityName = "Task";
    }

    public MapSqlParameterSource createFullParametersMapper(Task task) {
        return new MapSqlParameterSource().addValue("id", task.getVmID())
                .addValue("task_type", task.getTaskType().getValue())
                .addValue("backup_name", task.getBackupName());
    }

    public MapSqlParameterSource createIdParametersMapper(UUID id) {
        return new MapSqlParameterSource().addValue("id", id);
    }
}
