package ru.skuptsov.sphinx.console.spring.service.api;

import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.params.CollectionSearchParameters;

import java.util.List;

public interface CollectionService extends Service<Collection> {
    List<Collection> getCollections();
    List<Collection> getCollections(CollectionSearchParameters searchParameters);
    Collection getCollection(String name);
    Collection getCollection(Long id);
    void clearLogs(Long collectionId);
    void deleteAllCollectionData(Collection collection);
    List<Collection> getNameCollectionsByTemplateId(Long id);
    List<Collection> getFailureFalseCollections();
    List<Collection> getSimpleCollections();

    @Transactional(readOnly = true)
    List<Collection> getDistributedCollections(String collectionName);

    @Transactional(readOnly = true)
    void setNeedReloadBySimpleCollection(Collection collection);

    List<Collection> getCollectionsForSnippetCreation();
}
