package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.dao.api.ReplicaDao;
import ru.skuptsov.sphinx.console.spring.service.api.ReplicaService;

import java.util.List;

@Service
public class ReplicaServiceImpl extends AbstractSpringService<ReplicaDao, Replica> implements ReplicaService {

    @Override
    @Transactional(readOnly = true)
    public Long createReplicaNumber(String collectionName) {
        return getDao().createReplicaIndex(collectionName);
    }

    @Override
    @Transactional
    public Replica findReplicaByNumber(String collectionName, Long replicaNumber) {
        return getDao().findReplicaByNumber(collectionName, replicaNumber);
    }

    @Override
    @Transactional
    public List<Replica> findByCollectionName(String collectionName) {
        return getDao().findByCollectionName(collectionName);
    }

	@Override
	@Transactional(readOnly = true)
	public Long countReplicas(String collectionName) {
		return getDao().countReplicas(collectionName);
	}
}
