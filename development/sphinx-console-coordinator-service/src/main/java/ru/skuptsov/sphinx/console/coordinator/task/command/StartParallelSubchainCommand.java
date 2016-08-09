package ru.skuptsov.sphinx.console.coordinator.task.command;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.coordinator.task.command.db.CoordinatorCommand;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.TaskService;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.TaskServiceStrategyFactory;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;

@Component("START_PARALLEL_SUBCHAIN")
@Scope("prototype")
public class StartParallelSubchainCommand <T extends Task> extends CoordinatorCommand<T> implements AsyncCommand  {
	// необходим Async - ждём ответоа от callback

	@Autowired
    protected TaskServiceStrategyFactory<Task> taskServiceStrategyFactory;
	
	@Autowired
    private CollectionService collectionService;

	@Autowired
	private TaskExecutor executor; 
	
	@Override
    @SaveActivityLog
    public Status execute(final Task task) {
        logger.debug("START PARALLEL SUBCHAIN...");
        
        List<Task> subTasks = task.getSubTasks();
        
        if (subTasks == null) {
        	Status status = Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.DATA_INTEGRITY_FAILURE, task.getTaskUID(), "subtasks have not been provided");
        	return status;
        }
        
        logger.info("SUB TASKS: " + subTasks.size());

        final Status status = Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE, task.getTaskUID());
        for (final Task subTask : subTasks) {
        	if (subTask instanceof RebuildCollectionTask) {
        		subTask.setSphinxProcessType(SphinxProcessType.SEARCHING);
        	}
        	executor.execute(new Runnable() {
                public void run() {
                	logger.info("SUB TASK STATE: " + subTask.getState() + ", REPLICA NUMBER: " + subTask.getReplicaNumber() + ", TASK STATUS: " + subTask.getTaskStatus());
                    getTaskService(task.getTaskName()).execute(subTask);
                    logger.info("SUB TASK STATUS: " + subTask.getStatus());
                    subTask.setStatus(status);    
                }
            });

        	
        	
        }
        
        return status;
    }

	protected TaskService<Task> getTaskService(TaskName taskName) {
	    return taskServiceStrategyFactory.getTaskService(taskName.getTitle());
	}
	
	private List<Replica> getReplicas(String collectionName) {
	    Set<Replica> replicas = collectionService.getCollection(collectionName).getReplicas();
	    if (replicas != null) {
	        return new LinkedList<Replica>(replicas);
	    }
	    return null;
	}
	
}
