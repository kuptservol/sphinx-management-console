package ru.skuptsov.sphinx.console.spring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionRoleType;
import ru.skuptsov.sphinx.console.coordinator.model.DistributedCollectionNode;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.params.CollectionSearchParameters;
import ru.skuptsov.sphinx.console.dao.api.CollectionDao;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CollectionServiceImpl extends AbstractSpringService<CollectionDao, Collection> implements CollectionService {
	
	private final Logger logger = LoggerFactory.getLogger(CollectionServiceImpl.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ProcessService processService;

    @Override
	@Transactional(readOnly = true)
	public List<Collection> getCollections() {
		return getDao().getCollections();
	}

    @Override
    @Transactional(readOnly = true)
    public List<Collection> getCollections(CollectionSearchParameters searchParameters) {
        return getDao().getCollections(searchParameters);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection getCollection(String name) {
        return getDao().getCollection(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection getCollection(Long id) {
        return getDao().getCollection(id);
    }

	@Override
	@Transactional
	public void clearLogs(Long collectionId) {
		getDao().clearLogs(collectionId);
	}

    @Override
    @Transactional
    public void deleteAllCollectionData(Collection collection) {
        // остальные данные, связанные с коллекцией, удаляются через каскады в мапингах хибернейта
        // удаляем все сфинксовые процессы (нельзя удалять каскадами при удалении коллекции, т.к. конфигурации еще не удалены, и их нельзя удалить каскадами)
        Set<Long> configurationIds = new HashSet<Long>();
        
        logger.info("SPHINX PROCESSES: " + collection.getSphinxProcesses());
        
        for(SphinxProcess sphinxProcess : collection.getSphinxProcesses()){
        	logger.info("CONFIGURATION ID: " + sphinxProcess.getConfiguration().getId());
            processService.delete(sphinxProcess);
            configurationIds.add(sphinxProcess.getConfiguration().getId());
        }
        
        if (configurationIds.isEmpty()) {
        	List<Long> ids = configurationService.getConfigurationsIds(collection.getName());
        	logger.info("RETRIEVED IDS by NAME: " + ids);
        	if (ids != null) {
	        	for (Long id : ids) {
	        		configurationIds.add(id);
	        	}
            }
        }
        
        for(Long configurationId : configurationIds){
        	logger.info("DELETE CONFIGURTION WITH ID: " + configurationId);
        	configurationService.clearFieldMappings(configurationId);
            configurationService.deleteById(configurationId);
        }
        logger.info("DELETE CONFIGURTIONS, DONE");

        clearLogs(collection.getId());
        logger.info("CLEAR LOGS, DONE");

        getDao().delete(collection);
        logger.info("DELETE COLLECTION, DONE");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Collection> getNameCollectionsByTemplateId(Long id) {
        return getDao().getNameCollectionsByTemplateId(id);
    }

	@Override
	@Transactional(readOnly = true)
	public List<Collection> getFailureFalseCollections() {
		return getDao().getFailureFalseCollections();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Collection> getSimpleCollections() {
		return getDao().getSimpleCollections();
	}

    @Override
    @Transactional(readOnly = true)
    public List<Collection> getDistributedCollections(String collectionName) {
        return getDao().getDistributedCollections(collectionName);
    }

    @Override
    @Transactional
    public void setNeedReloadBySimpleCollection(Collection collection) {
        if (collection != null && collection.getCollectionType() == CollectionRoleType.SIMPLE &&  collection.getSimpleCollectionInNodes() != null && collection.getSimpleCollectionInNodes().size() != 0) {
            Set<DistributedCollectionNode> nodes = collection.getSimpleCollectionInNodes();
            if (nodes != null && nodes.size() != 0) {
                for (DistributedCollectionNode node : nodes) {
                    Collection distributedCollection = node.getDistributedCollection();
                    if (distributedCollection != null) {
                        distributedCollection.setNeedReload(true);
                        save(distributedCollection);
                    }
                }
            }
        }
    }

    @Override
	@Transactional(readOnly = true)
	public List<Collection> getCollectionsForSnippetCreation() {
		return getDao().getCollectionsForSnippetCreation();
	}
}
