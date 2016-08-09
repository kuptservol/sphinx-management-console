package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.AddDistributedCollectionTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;

@Component("INSERT_INTO_DISTRIBUTED_COLLECTION")
@Scope("prototype")
public class InsertIntoDistributedCollectionCommand extends DbCommand<AddDistributedCollectionTask> {
	@Autowired
    private CollectionService collectionService;

    @Override
    @SaveActivityLog
    public Status execute(AddDistributedCollectionTask task) {
        logger.debug("INSERT_INTO_DISTRIBUTED_COLLECTION STATE EXECUTION...");

        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());
        
        logger.info("COLLECTION: " + task.getCollection());
        
        collectionService.save(task.getCollection());

        return status;
    }
}
