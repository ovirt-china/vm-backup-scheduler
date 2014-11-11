package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

public abstract class CrudDAO<T> implements DAO<T>{
    protected RowMapper<T> instance;
    protected String entityName;

    public void save(T value) {
        DbFacade.getInstance().executeModification("save" + entityName, createFullParametersMapper(value));
    }

    public T get(UUID id) {
        List<T> result = (List<T>)DbFacade.getInstance().executeReadList("get" + entityName + "ById", instance, createIdParametersMapper(id));
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public void update(T value) {
        DbFacade.getInstance().executeModification("update" + entityName, createFullParametersMapper(value));
    }

    public void delete(UUID id) {
        DbFacade.getInstance().executeModification("delete" + entityName, createIdParametersMapper(id));
    }
}
