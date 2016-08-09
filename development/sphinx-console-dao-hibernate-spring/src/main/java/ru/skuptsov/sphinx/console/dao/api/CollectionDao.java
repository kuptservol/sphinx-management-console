package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.params.CollectionSearchParameters;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.List;


public interface CollectionDao extends Dao<Collection> {
	List<Collection> getCollections();
    List<Collection> getCollections(CollectionSearchParameters searchParameters);
    Collection getCollection(String name);
    Collection getCollection(Long id);
    void clearLogs(Long collectionId);
    List<Collection> getNameCollectionsByTemplateId(Long id);
    List<Collection> getFailureFalseCollections();
    List<Collection> getSimpleCollections();

    List<Collection> getDistributedCollections(String collectionName);

    List<Collection> getCollectionsForSnippetCreation();
}
