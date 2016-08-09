package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.task.DistributedTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;

@Component("UPDATE_DISTRIBUTED_STRUCTURE")
@Scope("prototype")
public class UpdateDistributedStructureCommand extends DbCommand<DistributedTask> {
    @Autowired
    private CollectionService collectionService;

    @Autowired
    private ProcessService processService;

    @Override
    @SaveActivityLog
    public Status execute(DistributedTask task) {
        logger.debug("UPDATE_DISTRIBUTED_STRUCTURE STATE EXECUTION...");
        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());

        Collection collection = collectionService.getCollection(task.getCollectionName());

        collection.setDistributedCollectionNodes(task.getNodes());

        collection.setNeedReload(false);
        collectionService.save(collection);

        return status;
    }
}
