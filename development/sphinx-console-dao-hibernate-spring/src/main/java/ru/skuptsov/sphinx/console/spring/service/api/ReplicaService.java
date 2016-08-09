package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.Replica;

import java.util.List;

public interface ReplicaService extends Service<Replica> {
    Long createReplicaNumber(String collectionName);
    Replica findReplicaByNumber(String collectionName, Long replicaNumber);
    List<Replica> findByCollectionName(String collectionName);
    Long countReplicas(String collectionName);
}
