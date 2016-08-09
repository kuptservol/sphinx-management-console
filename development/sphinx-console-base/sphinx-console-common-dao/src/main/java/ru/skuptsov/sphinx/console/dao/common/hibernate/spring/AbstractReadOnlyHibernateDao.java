package ru.skuptsov.sphinx.console.dao.common.hibernate.spring;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.coordinator.model.common.PagingCriteria;
import ru.skuptsov.sphinx.console.dao.common.hibernate.spring.util.HibernateInitializer;


public abstract class AbstractReadOnlyHibernateDao<T extends BaseEntity> {
    public static final int PARAMETER_LIMIT = 1000;
    public static final String ID_FILED_NAME = "id";
    public static final String CODE_FILED_NAME = "code";

    //
    private SessionFactory sessionFactory;
    private Class<T> persistentClass;

    private static Logger log = LoggerFactory.getLogger(AbstractReadOnlyHibernateDao.class);

    @SuppressWarnings("unchecked")
    public AbstractReadOnlyHibernateDao() {
        for (Type type = getClass().getGenericSuperclass(); type != null; ) {
            if (type instanceof ParameterizedType) {
                Object parameter = ((ParameterizedType) type).getActualTypeArguments()[0];

                if (parameter instanceof Class) {
                    persistentClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
                } else {
                    persistentClass = (Class<T>) ((ParameterizedType) ((TypeVariable<?>) parameter).getBounds()[0]).getRawType();
                }

                break;
            } else if (type instanceof Class) {
                type = ((Class<?>) type).getGenericSuperclass();
            }
        }
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSession() {
        Session session = sessionFactory.getCurrentSession();
        if (session instanceof SessionImplementor) {
            ((SessionImplementor)session).setAutoClear(true);
        }
        return session;
    }

    private void logSessionInfo(Session session){
        if(session instanceof SessionImpl){
            SessionImpl sessionImpl = (SessionImpl) session;
            PersistenceContext persistenceContext = sessionImpl.getPersistenceContext();
            int managedEntitiesNumber = persistenceContext.getEntitiesByKey().size();
            int collectionsInContext = persistenceContext.getCollectionEntries().size();
            String id = sessionImpl.getSessionIdentifier().toString();
            log.debug(String.format("Getting session with id %s... Session info: managed entities - %d, collections - %d", id, managedEntitiesNumber, collectionsInContext));
        }
    }

    @SuppressWarnings("unchecked")
    public T findById(Long id, String... properties) {
        T t = (T) getSession().get(getPersistentClass(), id);
        HibernateInitializer.initializeProperties(t, properties);
        return t;
    }

    protected <E> List<E> getSearchResults(Criteria criteria, int page, int pageSize) {
        criteria.setFirstResult(page * pageSize);
        if (pageSize != 0) {
            criteria.setMaxResults(pageSize);
        }

        @SuppressWarnings("unchecked") List<E> results = criteria.list();
        return results;
    }

    protected <E> List<E> getSearchResults(Query query, int page, int pageSize) {
        query.setFirstResult(page * pageSize);
        if (pageSize != 0) {
            query.setMaxResults(pageSize);
        }

        @SuppressWarnings("unchecked") List<E> results = query.list();
        return results;
    }

    protected <E> Disjunction buildInDisjunction(String propertyName, Collection<E> propertyValues) {
        List<E> list = new ArrayList<E>(propertyValues);
        Disjunction disjunction = Restrictions.disjunction();
        for(int index = 0; index < list.size();) {
            int nextIndex = index + PARAMETER_LIMIT < list.size() ? index + PARAMETER_LIMIT : list.size();
            disjunction.add(Restrictions.in(propertyName, list.subList(index, nextIndex)));
            index = nextIndex;
        }

        return disjunction;
    }

    protected String buildInSqlClauseTemplate(String propertyName, int propertyValuesListSize) {
        StringBuilder result = new StringBuilder("(");
        for(int index = 0; index < propertyValuesListSize;) {
            int nextIndex = index + PARAMETER_LIMIT < propertyValuesListSize ? index + PARAMETER_LIMIT : propertyValuesListSize;
            if (result.length() > 1) {
               result.append(" OR ");
            }
            result.append(propertyName).append(" IN ").append(getPositionedTemplate(nextIndex - index));
            index = nextIndex;
        }

        result.append(")");

        return result.toString();
    }

    private String getPositionedTemplate(int cnt) {
        StringBuilder result = new StringBuilder("(");
        for (int index = 0; index < cnt; index++) {
            result.append("?");
            if (index + 1 < cnt) {
                result.append(",");
            }
        }
        result.append(")");
        return result.toString();
    }

    protected org.hibernate.type.Type[] buildNTypesArray(org.hibernate.type.Type type, int cnt) {
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[cnt];
        for (int index=0; index < cnt; index++) {
            types[index] = type;
        }
        return types;
    }

    protected void applyPaging(PagingCriteria pagingCriteria, Criteria criteria) {
        criteria.setFirstResult((pagingCriteria.getPage() - 1) * pagingCriteria.getPageSize());
        if (pagingCriteria.getPageSize() != 0) {
            criteria.setMaxResults(pagingCriteria.getPageSize());
        }
    }

    protected boolean isAliasCreated(Criteria criteria, String hibernateAlias) {
        if (criteria == null || StringUtils.isEmpty(hibernateAlias)) return false;

        CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
        Iterator iterator = criteriaImpl.iterateSubcriteria();
        while (iterator.hasNext()) {
            Criteria aliasCriteria = (Criteria) iterator.next();
            if (hibernateAlias.equalsIgnoreCase(aliasCriteria.getAlias())) return true;
        }
        return false;
    }

    protected void addHibernateAlias(Criteria critera, String property, String alias, JoinType joinType) {
        if (isAliasCreated(critera, alias)) return;

        critera.createAlias(property, alias, joinType);
    }
}
