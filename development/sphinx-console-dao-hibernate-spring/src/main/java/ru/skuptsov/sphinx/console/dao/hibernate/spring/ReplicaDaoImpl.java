package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.dao.api.ReplicaDao;

import java.util.List;

@Repository
public class ReplicaDaoImpl extends AbstractCoordinatorHibernateDao<Replica> implements ReplicaDao {

    @Override
    public Long createReplicaIndex(String collectionName) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.createAlias("collection", "collection", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("collection.name", collectionName));
        criteria.setProjection(Projections.max("number"));
        Long result = (Long) criteria.uniqueResult();
        return result != null ? result  + 1 : 1;
    }

    @Override
    public Replica findReplicaByNumber(String collectionName, Long replicaNumber) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.createAlias("collection", "collection", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("number", replicaNumber));
        criteria.add(Restrictions.eq("collection.name", collectionName));
        return (Replica) criteria.uniqueResult();
    }

    @Override
    public List<Replica> findByCollectionName(String collectionName) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.createAlias("collection", "collection", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("collection.name", collectionName));
        criteria.addOrder(Order.asc("number"));
        return (List<Replica>) criteria.list();
    }

	@Override
	public Long countReplicas(String collectionName) {
		Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.createAlias("collection", "collection", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("collection.name", collectionName));
        criteria.addOrder(Order.asc("number"));
        
		return (Long) criteria.setProjection(Projections.countDistinct("id")).uniqueResult();
		
		
	}

}
