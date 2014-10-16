package org.ovirtChina.enginePlugin.vmBackupScheduler.dao;

import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface DAO<T> {
    abstract void save (T value);

    abstract T get(UUID id);

    abstract void update(T value);

    abstract void delete(UUID id);

    abstract MapSqlParameterSource createFullParametersMapper(T value);

    abstract MapSqlParameterSource createIdParametersMapper(UUID id);
}
