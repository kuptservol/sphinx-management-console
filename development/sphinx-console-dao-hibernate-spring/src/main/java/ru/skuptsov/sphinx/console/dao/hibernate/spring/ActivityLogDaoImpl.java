package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.skuptsov.sphinx.console.coordinator.model.ActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.TaskLogsSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.task.FullRebuildSnippetTask;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildSnippetsTask;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.dao.api.ActivityLogDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
public class ActivityLogDaoImpl extends AbstractCoordinatorHibernateDao<ActivityLog> implements ActivityLogDao {
	
	private Criteria buildHbmCriteria(ActivityLogSearchParameters parameters) {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass(), "al1");
       // hbmCriteria.createAlias("collection", "collection", JoinType.INNER_JOIN);
        if(StringUtils.isNotEmpty(parameters.getCollectionName())) {
            hbmCriteria.add(Restrictions.ilike("indexName", parameters.getCollectionName(), MatchMode.ANYWHERE));
        }
        if(parameters.getDateFrom() != null) {
            hbmCriteria.add(Restrictions.ge("startTime", parameters.getDateFrom()));
        }
        if(parameters.getDateTo() != null) {
            Date datePlus24h = add24hours(parameters.getDateTo());
        	
            hbmCriteria.add(Restrictions.le("endTime", datePlus24h));
        }
        if(!CollectionUtils.isEmpty(parameters.getTaskNames())) {
            hbmCriteria.add(Restrictions.in("taskName", TaskName.getTitles(parameters.getTaskNames())));
        }
        if(parameters.getTaskStatus() != null && !parameters.getTaskStatus().equals("")) {
            hbmCriteria.add(Restrictions.eq("taskStatus", parameters.getTaskStatus()));
        }
        
        
        
        DetachedCriteria maxId = DetachedCriteria.forClass(getPersistentClass(), "al2")
        		.add(Restrictions.eqProperty("al1.taskUid", "al2.taskUid"))
        		.setProjection(Projections.max("id"));
        
        hbmCriteria.add(Property.forName("id").eq(maxId));

        hbmCriteria.addOrder(Order.desc("id") );

        return hbmCriteria;
	}
	
	private Criteria buildHbmCriteriaTaskLog(TaskLogsSearchParameters parameters) {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());

        if(StringUtils.isNotEmpty(parameters.getTaskUid())) {
        	 hbmCriteria.add(Restrictions.eq("taskUid", parameters.getTaskUid()));
        }

        if(parameters.getReplicaNumber() != null) {
            hbmCriteria.add(Restrictions.eq("replicaNumber", parameters.getReplicaNumber()));
        }

        if(parameters.getProcessId() != null) {
            hbmCriteria.createAlias("process", "process", JoinType.LEFT_OUTER_JOIN);
            hbmCriteria.add(Restrictions.eq("process.id", parameters.getProcessId()));
        }

        if(parameters.getOperationType() != null) {
            hbmCriteria.add(Restrictions.eq("operationType", parameters.getOperationType()));
        }

        hbmCriteria.addOrder(Order.desc("id") );

        return hbmCriteria;
	}

    @Override
    public String getLastSnippetLogTaskUid(String collectionName) {
        List<String> taskNames = new ArrayList<String>();
        taskNames.add(RebuildSnippetsTask.TASK_NAME.getTitle());
        taskNames.add(FullRebuildSnippetTask.TASK_NAME.getTitle());

        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria
                .createAlias("collection", "collection", JoinType.INNER_JOIN)
                .setProjection(Projections.property("taskUid"))
                .add(Restrictions.eq("collection.name", collectionName))
                .add(Restrictions.in("taskName", taskNames))
                .addOrder(Order.desc("taskStartTime"))
                .setMaxResults(1);

        return (String) hbmCriteria.uniqueResult();
    }

    @Override
    public List<ActivityLog> find(ActivityLogSearchParameters parameters) {
    	Criteria hbmCriteria = buildHbmCriteria(parameters);
    	
    	if (parameters.getStart() != null && parameters.getLimit() != null) {
            hbmCriteria.setFirstResult(parameters.getStart()).setMaxResults(parameters.getLimit());
    	}
    	/*hbmCriteria.setFirstResult((parameters.getPage() - 1) * parameters.getLimit());
        if (parameters.getLimit() != 0) {
            hbmCriteria.setMaxResults(parameters.getLimit());
        }*/

    	/*if(parameters.getPage() != null && parameters.getLimit() != null) {
            hbmCriteria.setFirstResult(parameters.getPage())
                    .setMaxResults(parameters.getLimit());
        }*/
    	
        return hbmCriteria.list();
    }

	@Override
	public Long countActivityLog(ActivityLogSearchParameters parameters) {
		Criteria hbmCriteria = buildHbmCriteria(parameters);

		return (Long) hbmCriteria.setProjection(Projections.countDistinct("id")).uniqueResult();
	}

	@Override
	public List<ActivityLog> getTaskLog(TaskLogsSearchParameters parameters) {
        Criteria hbmCriteria = buildHbmCriteriaTaskLog(parameters);

        if(parameters.getLast() != null) {
            hbmCriteria.setProjection(Projections.max("id"));
            List<Long> result = hbmCriteria.list();
            return result.size() > 0 ? Collections.singletonList(this.findById(result.get(0))) : Collections.<ActivityLog>emptyList();
        }

    	if (parameters.getStart() != null && parameters.getLimit() != null) {
            hbmCriteria.setFirstResult(parameters.getStart()).setMaxResults(parameters.getLimit());
    	}
    	    	
        return hbmCriteria.list();

	}

	@Override
	public Long countTaskLog(TaskLogsSearchParameters parameters) {
		Criteria hbmCriteria = buildHbmCriteriaTaskLog(parameters);

		return (Long) hbmCriteria.setProjection(Projections.countDistinct("id")).uniqueResult();

	}

	@Override
	public ActivityLog getLast(String taskUid) {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass(), "al1");
        
        hbmCriteria.add(Restrictions.eq("taskUid", taskUid));
        

        DetachedCriteria maxId = DetachedCriteria.forClass(getPersistentClass(), "al2")
        		.add(Restrictions.eqProperty("al1.taskUid", "al2.taskUid"))
        		.setProjection(Projections.max("id"));
        
        hbmCriteria.add(Property.forName("id").eq(maxId));
		return (ActivityLog) hbmCriteria.uniqueResult();
	}

    @Override
    public List<ActivityLog> getSphinxProcessLogs(SphinxProcess sphinxProcess) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.add(Restrictions.eq("process", sphinxProcess));

        hbmCriteria.addOrder(Order.desc("id") );
        return hbmCriteria.list();
    }

    @Override
    public ActivityLog getLast(String collectionName, TaskName taskName, Date afterDate) {

        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass())
            .createAlias("collection", "collection", JoinType.INNER_JOIN)
            .add(Restrictions.eq("collection.name", collectionName))
            .add(Restrictions.eq("taskName", taskName.getTitle()));
            if (afterDate != null) {
                hbmCriteria.add(Restrictions.ge("date", afterDate));
            }
        hbmCriteria.setProjection(Projections.max("id"));

        List<Long> result = hbmCriteria.list();
        return CollectionUtils.isEmpty(result) || result.get(0) == null ? null : this.findById(result.get(0));
    }

	private Date add24hours(Date date) {
        date.setTime(date.getTime() + DateUtils.MILLIS_PER_DAY);
        return date;
    }
}
