package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.Delta;

public interface DeltaService extends Service<Delta> {
    Delta getByCollectionName(String collectionName);
}
