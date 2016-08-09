package ru.skuptsov.sphinx.console.coordinator.task.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.ReloadDistributedCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.ReloadDistributedCollectionTaskService;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;

@Service("reloadDistributedCollectionTaskService")
public class ReloadDistributedCollectionTaskServiceImpl extends AbstractTaskService<ReloadDistributedCollectionTask> implements ReloadDistributedCollectionTaskService {
	@Autowired
    private CollectionService collectionService;
	
	@Override
    public Status execute(ReloadDistributedCollectionTask task) {
		logger.info("EXECUTE: " + task.getState() + ", STATUS: " + task.getStatus());
	    Status status = super.execute(task);
	    
	    handleProcessing(task, status);
	    
	    return status;
	}
	
	@Override
    public synchronized Status handleAgentCallback(ReloadDistributedCollectionTask task, Status status) {
        logger.info("HANDLE AGENT CALLBACK: " + status + ", STATE: " + task.getState());
		Status nextStatus = super.handleAgentCallback(task, status);
		logger.info("HANDLE AGENT CALLBACK, NEXT: " +  nextStatus + ", STATE: " + task.getState());
        handleProcessing(task, nextStatus);
        
        return nextStatus;
    }
	
	private void handleProcessing(ReloadDistributedCollectionTask task, Status status) {
		    logger.info("ABOUT TO HANDLE PROCESSING OF ADD DISTRIBUTED COLLECTION STATE, FOR STATUS: " + status + ", STATE: " + task.getState());
		    Collection collection = collectionService.getCollection(task.getCollectionName());
		    if (collection != null) {
			    if (status != null && status.getCode() == Status.SUCCESS_CODE) {
	        		collection.setIsProcessingFailed(false);
	        	} else {
	        		collection.setIsProcessingFailed(true);
	        	}
	        	collectionService.save(collection);
		    }
	}
}