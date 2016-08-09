package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.DistributedCollectionNode;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.DistributedTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;

@Component("UPDATE_FULL_DISTRIBUTED_STRUCTURE")
@Scope("prototype")
public class UpdateFullDistributedStructureCommand extends DbCommand<DistributedTask> {
	@Autowired
    private CollectionService collectionService;
	
	@Override
    @SaveActivityLog
    public Status execute(DistributedTask task) {
        logger.debug("UPDATE_FULL_DISTRIBUTED_STRUCTURE EXECUTION...");
        
        logger.info("TASK: " + task);

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        
        Collection collection = collectionService.getCollection(task.getCollection().getName());
        
        collection.getDistributedCollectionNodes().clear();
        
        collectionService.save(collection);
        
        Set<DistributedCollectionNode> nodes = task.getCollection().getDistributedCollectionNodes();
        logger.info("NODES: " + nodes);
        if (nodes != null) {
	        for (DistributedCollectionNode node : nodes) {
	        	logger.info("NODE, ID: " + node.getId());
	        }
        }
        
        task.getCollection().setNeedReload(false);
        collectionService.save(task.getCollection());
        
        
        return status;
    }

}
