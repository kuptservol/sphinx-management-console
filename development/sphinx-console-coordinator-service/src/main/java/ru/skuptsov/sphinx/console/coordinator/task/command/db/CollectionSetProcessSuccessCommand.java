package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;

@Component("COLLECTION_SET_PROCESS_SUCCESS")
@Scope("prototype")
public class CollectionSetProcessSuccessCommand extends DbCommand<IndexNameTask> {
	@Autowired
    private CollectionService collectionService;

    @Override
    @SaveActivityLog
    public Status execute(IndexNameTask task) {
        logger.debug("COLLECTION_SET_PROCESS_SUCCESS STATE EXECUTION...");

        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());
        Collection collection = collectionService.getCollection(task.getCollectionName());
        collection.setIsProcessingFailed(false);
        collectionService.save(collection);
        
        return status;
    }
}
