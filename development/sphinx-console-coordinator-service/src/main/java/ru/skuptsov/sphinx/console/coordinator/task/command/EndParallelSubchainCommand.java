package ru.skuptsov.sphinx.console.coordinator.task.command;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.command.db.CoordinatorCommand;

@Component("END_PARALLEL_SUBCHAIN")
@Scope("prototype")
public class EndParallelSubchainCommand <T extends Task> extends CoordinatorCommand<T> implements SyncCommand {
	
	public static final long HOUR_5 = 18000000 ; 
	
	@Override
    @SaveActivityLog
    public Status execute(Task task) {
        logger.info("END PARALLEL SUBCHAIN: " + task.getTaskStatus());
        
        task.setStatus(Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.SUCCESS_CODE, task.getTaskUID()));
        
        long startTime = System.currentTimeMillis();
        long inProcessingTime = System.currentTimeMillis();
        
        while ((task.getProcessedSubTasks().get() != task.getSubTasks().size()) || inProcessingTime - startTime > HOUR_5) {
            logger.info("WAITING FOR SUBTASKS TO COMPLETE PARENT TASK: " + (inProcessingTime - startTime));
            
            try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				logger.error("ERROR OCCURED: ", e);
			}
            
            inProcessingTime = System.currentTimeMillis();

        }
        
        logger.info("FINISH PARENT TASK: " + task.getStatus() + ", STATE: " + task.getState());
        
        List<Task> failures = getFailures(task.getSubTasks());
        
        String stackTrace = "";
    	for (Task failureTask : failures) {
    		logger.info("FAILURE STACKTRACE: " + failureTask.getStatus().getStackTrace());
    		stackTrace += failureTask.getSearchServerName() + ": " + failureTask.getStatus().getStackTrace() + System.getProperty("line.separator");
    	}
        
        if (task.getStatus().getCode() == Status.SUCCESS_CODE) {
        
	        logger.info("FAILURE TASKS: " + failures);
	       
	        if (!failures.isEmpty()) {
	        	task.setShouldBeLogged(true);
	        	task.setTaskStatus(TaskStatus.FAILURE);
	        	
	        	Status status = Status.build(Status.SystemInterface.COORDINATOR_AGENT, Status.StatusCode.FAILURE_EXECUTE_SPHINX_COMMAND, task.getTaskUID());
	        	
	        	status.setStackTrace(stackTrace);
	        	
	        	task.setStatus(status);
	        	return status;
	        }
        }
        
        if (!stackTrace.equals("")) {
        	 task.getStatus().setStackTrace(stackTrace);	
        }
        
        return task.getStatus();
    }
	
	private boolean containsFailure(List<Task> tasks) {
		for (Task task : tasks) {
			logger.info("SUB TASK, STATUS: " + task.getTaskStatus());
			if (task.getTaskStatus() == TaskStatus.FAILURE) {
				return true;
			}
		}
		return false;
	}
	
	private Task getFailure(List<Task> tasks) {
		for (Task task : tasks) {
			logger.info("SUB TASK, STATUS: " + task.getTaskStatus());
			if (task.getTaskStatus() == TaskStatus.FAILURE) {
				return task;
			}
		}
		return null;
	}
	
	private List<Task> getFailures(List<Task> tasks) {
		List<Task> failures = new ArrayList<Task>();
		for (Task task : tasks) {
			logger.info("SUB TASK, STATUS: " + task.getTaskStatus());
			if (task.getTaskStatus() == TaskStatus.FAILURE) {
				failures.add(task);
			}
		}
		return failures;
	}

}
