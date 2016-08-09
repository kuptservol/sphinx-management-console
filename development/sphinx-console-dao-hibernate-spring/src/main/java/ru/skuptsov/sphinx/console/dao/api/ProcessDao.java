package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.List;


public interface ProcessDao extends Dao<SphinxProcess> {
	List<SphinxProcess> getProcesses();

    List<SphinxProcess> getByCollectionNameAndType(String collectionName, SphinxProcessType type);
    List<SphinxProcess> getByCollectionNameAndType(Long serverId, String collectionName, SphinxProcessType type);
    List<SphinxProcess> getByCollectionNameAndType(String serverName, String collectionName, SphinxProcessType type);
    List<SphinxProcess> findSearchProcesses(String collectionName);
    SphinxProcess findSearchProcess(String collectionName, Long replicaNumber);

    SphinxProcess findSearchProcess(String serverIp, Integer distributedPort);

    SphinxProcess findIndexingProcess(String collectionName);
    SphinxProcess findFullIndexingProcess(String collectionName);

    SphinxProcess getByReplica(Replica replica);
    List<SphinxProcess> getByServerId(Long serverId);
    List<SphinxProcess> getByServerName(String serverName);
    void clearDistributedCollectionAgents(Long processId);
}
