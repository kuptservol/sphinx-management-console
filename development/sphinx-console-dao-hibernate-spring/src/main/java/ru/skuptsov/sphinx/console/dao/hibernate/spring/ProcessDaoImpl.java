package ru.skuptsov.sphinx.console.dao.hibernate.spring;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.dao.api.ProcessDao;

import java.math.BigInteger;
import java.util.List;

@Repository
public class ProcessDaoImpl extends AbstractCoordinatorHibernateDao<SphinxProcess> implements ProcessDao {
	private static final String CLEAR_DISTRIBUTED_COLLECTION_AGENTS_QUERY = "delete from sphinx.console.DISTRIBUTED_COLLECTION_AGENT where SPHINX_PROCESS_ID = :process_id";
	
	@Override
	public List<SphinxProcess> getProcesses() {
		Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
		return hbmCriteria.list();
	}

    @Override
    public List<SphinxProcess> getByCollectionNameAndType(String collectionName, SphinxProcessType type) {
        return getByCollectionNameAndType((String)null, collectionName, type);
    }

    @Override
    public List<SphinxProcess> getByCollectionNameAndType(Long serverId, String collectionName, SphinxProcessType type) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        if(serverId != null && serverId > 0) {
            hbmCriteria.add(Restrictions.eq("server.id", serverId));
        }
        hbmCriteria.add(Restrictions.eq("indexName", collectionName));
        hbmCriteria.add(Restrictions.eq("type", type));
        return hbmCriteria.list();
    }

    @Override
    public List<SphinxProcess> getByCollectionNameAndType(String serverName, String collectionName, SphinxProcessType type) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        if(serverName != null && serverName.length() > 0) {
            hbmCriteria.add(Restrictions.eq("server.name", serverName));
        }
        hbmCriteria.add(Restrictions.eq("indexName", collectionName));
        hbmCriteria.add(Restrictions.eq("type", type));
        return hbmCriteria.list();
    }

    @Override
    public SphinxProcess getByReplica(Replica replica) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.add(Restrictions.and(Restrictions.eq("indexName", replica.getCollection().getName()),
                Restrictions.eq("replica", replica), Restrictions.eq("type", SphinxProcessType.SEARCHING)));
        return (SphinxProcess) hbmCriteria.uniqueResult();
    }

    @Override
    public List<SphinxProcess> getByServerId(Long serverId) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.add(Restrictions.eq("server.id", serverId));
        return (List<SphinxProcess>) hbmCriteria.list();
    }

    @Override
    public List<SphinxProcess> getByServerName(String serverName) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.createAlias("server", "server", JoinType.INNER_JOIN);
        hbmCriteria.add(Restrictions.eq("server.name", serverName));
        return (List<SphinxProcess>) hbmCriteria.list();
    }

    @Override
    public List<SphinxProcess> findSearchProcesses(String collectionName) {
        return getByCollectionNameAndType(collectionName, SphinxProcessType.SEARCHING);
    }

    @Override
    public SphinxProcess findSearchProcess(String collectionName, Long replicaNumber) {
        Criteria hbmCriteria = getSession().createCriteria(getPersistentClass());
        hbmCriteria.createAlias("replica", "replica", JoinType.INNER_JOIN);
        hbmCriteria.add(Restrictions.and(Restrictions.eq("indexName", collectionName),
                Restrictions.eq("replica.number", replicaNumber), Restrictions.eq("type", SphinxProcessType.SEARCHING)));
        return (SphinxProcess) hbmCriteria.uniqueResult();
    }

    @Override
    public SphinxProcess findSearchProcess(String serverIp, Integer distributedPort) {

        String queryStr = "SELECT sphinx_process_id FROM sphinx.console.COLLECTION_REPLICA_V\n" +
                "where (server_ip = :server_ip)\n" +
                "and (distributed_listen_port = :distributed_listen_port)\n";

        SQLQuery query = getSession().createSQLQuery(queryStr);
        query.setParameter("server_ip", serverIp);
        query.setParameter("distributed_listen_port", distributedPort);

        Object result = query.uniqueResult();

        SphinxProcess sphinxProcess;
        if(result == null){
            sphinxProcess = null;
        } else{
            Long sphinxProcessId = ((BigInteger)query.uniqueResult()).longValue();
            sphinxProcess = findById(sphinxProcessId);
        }

        return sphinxProcess;

    }

    @Override
    public SphinxProcess findIndexingProcess(String collectionName) {
        List<SphinxProcess> processes = getByCollectionNameAndType(collectionName, SphinxProcessType.INDEXING);
        return processes.size() > 0 ? processes.get(0) : null;
    }

    @Override
    public SphinxProcess findFullIndexingProcess(String collectionName) {
        List<SphinxProcess> processes = getByCollectionNameAndType(collectionName, SphinxProcessType.FULL_INDEXING);
        return processes.size() > 0 ? processes.get(0) : null;
    }

	@Override
	public void clearDistributedCollectionAgents(Long processId) {
		SQLQuery query = getSession().createSQLQuery(CLEAR_DISTRIBUTED_COLLECTION_AGENTS_QUERY);
		query.setParameter("process_id", processId);
    	query.executeUpdate();
	}
}
