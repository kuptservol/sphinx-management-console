package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.dao.common.hibernate.spring.AbstractHibernateDao;


public abstract class AbstractCoordinatorHibernateDao <T extends BaseEntity> extends AbstractHibernateDao<T> {
	@Override
    @Autowired
    @Qualifier("ru.skuptsov.sphinx.console.dao.hibernate.spring.coordinator.sessionFactory")
    public void setSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }
}
