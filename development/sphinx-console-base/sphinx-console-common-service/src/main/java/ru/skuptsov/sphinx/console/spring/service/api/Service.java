package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;


public interface Service<T extends BaseEntity> {

    T findById(Long id, String... properties);

    T save(T entity);

    void delete(T entity);

    void deleteById(Long id);
}
