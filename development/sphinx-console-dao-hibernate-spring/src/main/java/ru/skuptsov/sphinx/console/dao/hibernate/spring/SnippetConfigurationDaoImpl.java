package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.params.SnippetSearchParameters;
import ru.skuptsov.sphinx.console.dao.api.SnippetConfigurationDao;

@Repository
public class SnippetConfigurationDaoImpl extends AbstractCoordinatorHibernateDao<SnippetConfiguration> implements SnippetConfigurationDao {

    private Criteria buildHbmCriteria(SnippetSearchParameters parameters) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());

        hbmCriteria.createAlias("collection", "collection", JoinType.INNER_JOIN);
        if (StringUtils.isNotEmpty(parameters.getCollectionName())) {
            hbmCriteria.add(Restrictions.ilike("collection.name", parameters.getCollectionName(), MatchMode.ANYWHERE));
        }

        hbmCriteria.addOrder(Order.desc("id"));

        return hbmCriteria;
    }

    @Override
    public List<SnippetConfiguration> find(SnippetSearchParameters parameters) {
        Criteria hbmCriteria = buildHbmCriteria(parameters);

        if (parameters.getStart() != null && parameters.getLimit() != null) {
            hbmCriteria.setFirstResult(parameters.getStart()).setMaxResults(parameters.getLimit());
        }

        return hbmCriteria.list();
    }

    @Override
    public Long count(SnippetSearchParameters parameters) {
        Criteria hbmCriteria = buildHbmCriteria(parameters);

        return (Long) hbmCriteria.setProjection(Projections.countDistinct("id")).uniqueResult();
    }

    @Override
    public List<SnippetConfiguration> getSnippets() {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        return hbmCriteria.list();
    }
}
