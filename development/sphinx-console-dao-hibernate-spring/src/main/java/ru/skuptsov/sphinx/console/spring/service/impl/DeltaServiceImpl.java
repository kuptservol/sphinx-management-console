package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.Delta;
import ru.skuptsov.sphinx.console.dao.api.DeltaDao;
import ru.skuptsov.sphinx.console.spring.service.api.DeltaService;

@Service
public class DeltaServiceImpl extends AbstractSpringService<DeltaDao, Delta> implements DeltaService {
    @Override
    @Transactional(readOnly = true)
    public Delta getByCollectionName(String collectionName) {
        return getDao().findByCollectionName(collectionName);
    }
}
