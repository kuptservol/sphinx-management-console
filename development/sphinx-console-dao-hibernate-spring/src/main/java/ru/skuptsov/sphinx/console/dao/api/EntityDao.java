package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

public interface EntityDao extends Dao<BaseEntity> {

    boolean existsById(Long id, Class clazz);

    boolean existsByField(Class clazz, String fieldName, Object fieldValue);

}
