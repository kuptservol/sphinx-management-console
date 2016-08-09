package ru.skuptsov.sphinx.console.admin.dao.hibernate.spring;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.annotation.Lazy;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.dao.common.hibernate.spring.AbstractHibernateDao;


public abstract class AbstractCoordinatorHibernateDao <T extends BaseEntity> extends AbstractHibernateDao<T> {
	@Override
    @Autowired
    @Lazy
    @Qualifier("ru.skuptsov.sphinx.console.admin.dao.hibernate.spring.sessionFactory")
    public void setSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }
}
