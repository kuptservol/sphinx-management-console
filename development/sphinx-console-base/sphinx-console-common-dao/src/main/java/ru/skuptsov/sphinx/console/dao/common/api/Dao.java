package ru.skuptsov.sphinx.console.dao.common.api;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;


public interface Dao<T extends BaseEntity> {
	/**
     * Загружает сущность по заданному id и инициализирует указанные поля.
     * @param id - идентификатор сущности.
     * @param properties - поля для инициализации
     * @return сущность
     */
    T findById(Long id, String... properties);

    /**
     * Сохраняет или обновляет сущность если уже есть запись в базе.
     * @param entity - Объект сущности для сохранения
     */
    void saveOrUpdate(T entity);

    /**
     * Сохраняет сущность.
     * @param entity Объект сущности для сохранения
     * */
    Long add(T entity);

    /**
     * Удаляет сущность.
     * @param entity Объект сущности для удаления
     * */
    void delete(T entity);

    /**
     * Удаляет сущность.
     * @param id id сущности для удаления
     * */
    void deleteById(Long id);

    T merge(T entity);

    void evict(T entity);

    T findAndLock(Long id);
}
