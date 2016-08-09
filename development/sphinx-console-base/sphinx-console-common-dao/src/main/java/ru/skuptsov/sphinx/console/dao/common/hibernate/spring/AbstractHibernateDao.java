package ru.skuptsov.sphinx.console.dao.common.hibernate.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;


public abstract class AbstractHibernateDao <T extends BaseEntity> extends AbstractReadOnlyHibernateDao<T> implements Dao<T> {
	public void saveOrUpdate(T entity) {
        getSession().saveOrUpdate(entity);
    }

    @Override
    public Long add(T entity) {
        return (Long) getSession().save(entity);
    }

    public final void delete(T entity) {
        getSession().delete(entity);
    }

    @Override
    public final void deleteById(Long id) {
        Session session = getSession();
        Object o = session.get(getPersistentClass(), id);
        if (o != null) {
            session.delete(o);
        }
    }

    @Override
    public T merge(T entity) {
        return (T) getSession().merge(entity);
    }

    @Override
    public void evict(T entity) {
        getSession().evict(entity);
    }

    @Override
    public T findAndLock(Long id) {
        return (T) getSession().get(getPersistentClass(), id, LockOptions.UPGRADE);
    }

    
    protected List<String> enumsAsStrings(Enum[] enums) {
        if (ArrayUtils.isEmpty(enums)) return null;

        List<String> enumsAsStringList = new ArrayList<String>(enums.length);

        for (Enum enumElement : enums) {
            enumsAsStringList.add(enumElement.name());
        }

        return enumsAsStringList;
    }

    /**
     * Огромная благодарность и поклон Денису Згурскому, чей светлейший ум и зоркий глаз при помощи смекалки, упорства и гугла
     * нашел этот чудо способ декорировать запросы хибернейта предврительными выборками, позволяющий сделать что-то типа:
     *
     * with tmp as (select.......)
     * select from ... where column1 in (select someting from tmp)
     *
     * @param criteria - Критерий хибера
     * @param tmpSelection - запрос предварительной выборки
     * @return
     */
    protected Query decorateCriteriaWithTmpSelection(Criteria criteria, String tmpSelection) {
        CriteriaImpl c = (CriteriaImpl) criteria;
        SessionImplementor session = c.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        String[] implementors = factory.getImplementors(c.getEntityOrClassName());
        CriteriaQueryTranslator translator = new CriteriaQueryTranslator(factory, c, c.getEntityOrClassName(), CriteriaQueryTranslator.ROOT_SQL_ALIAS);

        CriteriaJoinWalker walker = new CriteriaJoinWalker((OuterJoinLoadable) factory.getEntityPersister(implementors[0]),
                translator,
                factory,
                c,
                c.getEntityOrClassName(),
                c.getSession().getLoadQueryInfluencers());

        String sql = tmpSelection + walker.getSQLString();

        QueryParameters queryParameters = translator.getQueryParameters();

        return getSession().createSQLQuery(sql).setParameters(queryParameters.getPositionalParameterValues(), queryParameters.getPositionalParameterTypes());
    }

    protected CriteriaQuery getQuery(Criteria criteria) {
        CriteriaImpl c = (CriteriaImpl) criteria;
        SessionImplementor session = c.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        return new CriteriaQueryTranslator(factory, c, c.getEntityOrClassName(), CriteriaQueryTranslator.ROOT_SQL_ALIAS);
    }
}
