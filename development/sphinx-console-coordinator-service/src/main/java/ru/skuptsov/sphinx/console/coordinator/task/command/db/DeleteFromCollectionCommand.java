package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.DeleteCollectionTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 13.08.14
 * Time: 19:34
 * To change this template use File | Settings | File Templates.
 */
@Component("DELETE_COLLECTION")
@Scope("prototype")
public class DeleteFromCollectionCommand extends DbCommand<DeleteCollectionTask> {
    @Autowired
    private CollectionService collectionService;

    @Override
    @SaveActivityLog
    public Status execute(DeleteCollectionTask task) {
        logger.debug("DELETE_COLLECTION STATE EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        
        if (task.getCollection() != null && task.getCollection().getId() != null) {
            Collection savedCollection = collectionService.findById(task.getCollection().getId());
            collectionService.setNeedReloadBySimpleCollection(savedCollection);
            collectionService.deleteAllCollectionData(savedCollection);
        }

        return status;
    }
}
