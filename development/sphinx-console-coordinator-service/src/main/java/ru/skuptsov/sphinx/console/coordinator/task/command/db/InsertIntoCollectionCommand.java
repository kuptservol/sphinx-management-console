package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.AddCollectionTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 23:20
 * To change this template use File | Settings | File Templates.
 */
@Component("INSERT_INTO_COLLECTION")
@Scope("prototype")
public class InsertIntoCollectionCommand extends DbCommand<AddCollectionTask> {
	@Autowired
    private CollectionService collectionService;

    @Override
    @SaveActivityLog
    public Status execute(AddCollectionTask task) {
        logger.debug("INSERT_INTO_COLLECTION STATE EXECUTION...");

        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());
        collectionService.save(task.getCollection());

        return status;
    }
}
