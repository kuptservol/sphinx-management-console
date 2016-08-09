package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.dao.api.ProcessDao;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ReplicaService;

import java.util.List;

@Service
public class ProcessServiceImpl extends AbstractSpringService<ProcessDao, SphinxProcess> implements ProcessService {
	public static final Long FIRST_REPLICA = 1L; 
	
	@Autowired
    private ReplicaService replicaService;
	
	@Override
	@Transactional(readOnly = true)
	public List<SphinxProcess> getProcesses() {
		return getDao().getProcesses();
	}

    @Override
    @Transactional(readOnly = true)
    public List<SphinxProcess> findByServerId(Long serverId) {
        return getDao().getByServerId(serverId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SphinxProcess> findByServerName(String serverName) {
        return getDao().getByServerName(serverName);
    }

    @Override
    @Transactional(readOnly = true)
    public Configuration getConfiguration(Long processId) {
        SphinxProcess process = getDao().findById(processId);
        return (process != null) ? process.getConfiguration() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public SphinxProcess findByReplica(Replica replica) {
        return getDao().getByReplica(replica);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SphinxProcess> findByCollectionNameAndType(Long serverId, String collectionName, SphinxProcessType type) {
        return getDao().getByCollectionNameAndType(serverId, collectionName, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SphinxProcess> findByCollectionNameAndType(String serverName, String collectionName, SphinxProcessType type) {
        return getDao().getByCollectionNameAndType(serverName, collectionName, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SphinxProcess> findByCollectionNameAndType(String collectionName, SphinxProcessType type) {
        return getDao().getByCollectionNameAndType(collectionName, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SphinxProcess> findSearchProcesses(String collectionName) {
        return getDao().findSearchProcesses(collectionName);
    }

    @Override
    @Transactional(readOnly = true)
    public SphinxProcess findSearchProcess(String collectionName, Long replicaNumber) {
        if (replicaNumber == FIRST_REPLICA) {
            //отсортированный список реплик
            List<Replica> replicas = replicaService.findByCollectionName(collectionName);
            if (replicas != null && !replicas.isEmpty()) {
                replicaNumber = replicas.get(0).getNumber();
            }

        }
        return getDao().findSearchProcess(collectionName, replicaNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public SphinxProcess findSearchProcess(String serverIp, Integer distributedPort) {
        return getDao().findSearchProcess(serverIp, distributedPort);
    }

    @Override
    @Transactional(readOnly = true)
    public SphinxProcess findIndexingProcess(String collectionName) {
        return getDao().findIndexingProcess(collectionName);
    }

    @Override
    @Transactional(readOnly = true)
    public SphinxProcess findFullIndexingProcess(String collectionName) {
        return getDao().findFullIndexingProcess(collectionName);
    }

	@Override
	@Transactional
	public void clearDistributedCollectionAgents(Long processId) {
		getDao().clearDistributedCollectionAgents(processId);
		
	}
}
