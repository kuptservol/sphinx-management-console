package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.List;

public interface ReplicaDao extends Dao<Replica> {

    Long createReplicaIndex(String collectionName);
    Replica findReplicaByNumber(String collectionName, Long replicaNumber);
    List<Replica> findByCollectionName(String collectionName);
    Long countReplicas(String collectionName);
}
