package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.dao.api.ScheduledTaskDao;

import java.util.List;

@Repository
public class ScheduledTaskDaoImpl extends AbstractCoordinatorHibernateDao<ScheduledTask> implements ScheduledTaskDao {

	
	@Override
	public List<ScheduledTask> getScheduledTasks() {
		
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
		return hbmCriteria.list();
	}
	
	@Override
	public List<ScheduledTask> getScheduledTasks(ScheduledTaskType type) {
		
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
		hbmCriteria.add(Restrictions.eq("type", type));
		return hbmCriteria.list();
	}

    @Override
    public ScheduledTask getByCollectionName(String collectionName, ScheduledTaskType type) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.createAlias("collection", "collection", JoinType.INNER_JOIN);
        hbmCriteria.add(Restrictions.eq("collection.name", collectionName));
        hbmCriteria.add(Restrictions.eq("type", type));
        return (ScheduledTask) hbmCriteria.uniqueResult();
    }
}
