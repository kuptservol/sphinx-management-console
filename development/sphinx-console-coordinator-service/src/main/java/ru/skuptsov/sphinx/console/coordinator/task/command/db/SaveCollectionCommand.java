package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.CollectionRoleType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;

@Component("SAVE_COLLECTION")
@Scope("prototype")
public class SaveCollectionCommand extends DbCommand<ProcessTask> {

    @Autowired
    private CollectionService collectionService;

    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        
        if (task.getCollection().getCollectionType() == CollectionRoleType.SIMPLE) {
            task.getCollection().getDelta().setCollection(task.getCollection());
        }
        
        
        Collection collection = collectionService.getCollection(task.getCollectionName());
        
        if (collection != null && collection.getCollectionType() == CollectionRoleType.SIMPLE) {
        	task.getCollection().setLastIndexingTime(collection.getLastIndexingTime());
        	task.getCollection().setNextIndexingTime(collection.getNextIndexingTime());
        	task.getCollection().setLastMergeTime(collection.getLastMergeTime());
        	task.getCollection().setNextMergeTime(collection.getNextMergeTime());
        }
        
        collectionService.save(task.getCollection());
        return status;
    }
}
