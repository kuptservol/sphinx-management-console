package ru.skuptsov.sphinx.console.spring.service.api;

public interface EntityService {

    boolean existsById(Long id, Class clazz);

    boolean existsByField(Class clazz, String fieldName, Object fieldValue);

}
