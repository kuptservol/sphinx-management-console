package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;
import ru.skuptsov.sphinx.console.spring.service.api.Service;


public abstract class AbstractSpringService<T extends Dao, E extends BaseEntity> implements Service<E> {

    private T dao;

    protected T getDao() {
        return dao;
    }

    @Autowired
    protected void setDao(T dao) {
        this.dao = dao;
    }

    @Transactional
    public E save(E entity) {
        dao.saveOrUpdate(entity);
        return entity;
    }

    @Transactional
    public E findById(Long id, String... properties) {
        return (E) dao.findById(id, properties);
    }

    @Transactional
    public void delete(E entity){
        dao.delete(entity);
    }

    @Transactional
    public void deleteById(Long id){
        dao.deleteById(id);
    }
}
