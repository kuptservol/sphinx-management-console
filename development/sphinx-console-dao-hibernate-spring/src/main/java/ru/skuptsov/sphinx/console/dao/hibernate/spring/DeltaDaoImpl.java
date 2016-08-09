package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.Delta;
import ru.skuptsov.sphinx.console.dao.api.DeltaDao;

@Repository
public class DeltaDaoImpl extends AbstractCoordinatorHibernateDao<Delta> implements DeltaDao {
    @Override
    @Transactional(readOnly = true)
    public Delta findByCollectionName(String collectionName) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.createAlias("collection", "collection", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("collection.name", collectionName));
        return (Delta)criteria.uniqueResult();
    }
}
