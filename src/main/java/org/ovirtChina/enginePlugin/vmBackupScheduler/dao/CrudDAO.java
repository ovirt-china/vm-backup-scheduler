package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

public abstract class CrudDAO<T> implements DAO<T>{
    protected RowMapper<T> instance;
    protected String entityName;

    public void save(T value) {
        DbFacade.getInstance().executeModification("save" + entityName, createFullParametersMapper(value));
    }

    public T get(UUID id) {
        DbFacade.getInstance().executeReadList("get" + entityName + "ById", instance, createIdParametersMapper(id));
        return null;
    }

    public void update(T value) {
        DbFacade.getInstance().executeModification("update" + entityName, createFullParametersMapper(value));
    }

    public void delete(UUID id) {
        DbFacade.getInstance().executeModification("delete" + entityName, createIdParametersMapper(id));
    }
}
