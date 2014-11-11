package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.ovirtChina.enginePlugin.vmBackupScheduler.common.Task;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class TaskDAOImpl extends CrudDAO<Task>{

    public TaskDAOImpl() {
        instance = new RowMapper<Task>() {

            public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Task(((UUID)rs.getObject("id")),
                    rs.getInt("task_status"),
                    rs.getInt("task_type"),
                    rs.getString("backup_name"));
            }
        };
        entityName = "Task";
    }

    public MapSqlParameterSource createFullParametersMapper(Task task) {
        return new MapSqlParameterSource().addValue("v_id", task.getVmID())
                .addValue("v_task_status", task.getTaskStatus())
                .addValue("v_task_type", task.getTaskType())
                .addValue("v_backup_name", task.getBackupName());
    }

    public MapSqlParameterSource createIdParametersMapper(UUID id) {
        return new MapSqlParameterSource().addValue("v_id", id);
    }
}
