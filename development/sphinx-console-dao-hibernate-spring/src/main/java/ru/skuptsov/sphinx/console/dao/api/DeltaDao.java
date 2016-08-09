package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.Delta;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

/**
 * Created by Andrey on 21.11.2014.
 */
public interface DeltaDao  extends Dao<Delta> {
    Delta findByCollectionName(String collectionName);
}
