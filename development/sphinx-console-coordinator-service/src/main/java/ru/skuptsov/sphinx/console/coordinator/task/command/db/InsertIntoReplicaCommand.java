package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.CreateReplicaTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;

@Component("INSERT_INTO_REPLICA")
@Scope("prototype")
public class InsertIntoReplicaCommand extends DbCommand<CreateReplicaTask> {
	@Autowired
    private CollectionService collectionService;

    @Override
    @SaveActivityLog
    public Status execute(CreateReplicaTask task) {
        logger.debug("INSERT_INTO_REPLICA STATE EXECUTION...");
        Collection collection = collectionService.getCollection(task.getCollectionName());
        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());
        Replica replica = new Replica();
        replica.setNumber(task.getReplicaNumber());
        replica.setCollection(collection);
        collection.getReplicas().add(replica);
        collectionService.save(collection);

        collectionService.setNeedReloadBySimpleCollection(collection);

        return status;
    }
}
