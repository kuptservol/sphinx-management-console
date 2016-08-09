package ru.skuptsov.sphinx.console.spring.service.api;

import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;

import java.util.List;

public interface ProcessService extends Service<SphinxProcess> {
    List<SphinxProcess> getProcesses();
    public List<SphinxProcess> findByServerId(Long serverId);
    public List<SphinxProcess> findByServerName(String serverName);
    Configuration getConfiguration(Long processId);
    List<SphinxProcess> findSearchProcesses(String collectionName);

    @Transactional(readOnly = true)
    SphinxProcess findSearchProcess(String serverIp, Integer distributedPort);

    SphinxProcess findSearchProcess(String collectionName, Long replicaNumber);
    SphinxProcess findIndexingProcess(String collectionName);
    SphinxProcess findFullIndexingProcess(String collectionName);
    List<SphinxProcess> findByCollectionNameAndType(Long serverId, String collectionName, SphinxProcessType type);
    List<SphinxProcess> findByCollectionNameAndType(String serverName, String collectionName, SphinxProcessType type);
    List<SphinxProcess> findByCollectionNameAndType(String collectionName, SphinxProcessType type);
    SphinxProcess findByReplica(Replica replica);
    void clearDistributedCollectionAgents(Long processId);
}
