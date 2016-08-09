package ru.skuptsov.sphinx.console.coordinator.agent.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import ru.skuptsov.sphinx.console.coordinator.callback.service.api.CoordinatorCallbackService;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxServiceStrategyFactory;

import java.io.IOException;
import java.text.MessageFormat;

public abstract class CommandService {
	protected Logger logger;
    protected Logger getLogger() {
        if(logger == null) {
            logger = LoggerFactory.getLogger(CommandService.class);
        }
        return logger;
    }

    protected org.springframework.jmx.access.MBeanProxyFactoryBean coordinatorCallbackServiceClient;

    @Autowired
    private SphinxServiceStrategyFactory sphinxServiceStrategyFactory;
    
    @Autowired
    private RetryTemplate retryTemplate;
    
    @Autowired
    private org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;
    
    protected CoordinatorCallbackService getCoordinatorCallbackServiceClient() {
    	if (coordinatorCallbackServiceClient != null) {
            return (CoordinatorCallbackService)coordinatorCallbackServiceClient.getObject();
    	}
    	return null;
    }

    protected CommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        this.coordinatorCallbackServiceClient = coordinatorCallbackServiceClient;
    }

    @Async
    public void execute(Task task, Object... params) {
        getLogger().info(MessageFormat.format("ABOUT TO EXECUTE COMMANDS, IN: {0}. For task: {1}. Task state: {2}", this, task, task.getState()));
        getLogger().info("TASK EXECUTOR THREADS: " + taskExecutor.getActiveCount() + ", " + taskExecutor.getKeepAliveSeconds() + ", " + taskExecutor.getPoolSize());
        executeCommands(task, params);
        callback(task);
    }

    protected void executeCommands(Task task, Object... params) {
        try {
            getLogger().info("EXECUTING COMMANDS...");
            SphinxService sphinxService = sphinxServiceStrategyFactory.getSphinxService(task.getSphinxProcessType().name());
            getLogger().info("SPHINX SERVICE: " + sphinxService);
            String processName = (String)params[0];
            getLogger().info("PROCESS NAME: " + processName);
            if(processName != null) {
                executeCommands(sphinxService, processName, params);
            }
        } catch(Throwable e) {
            Status failureStatus = Status.build(Status.SystemInterface.COORDINATOR_AGENT, Status.StatusCode.FAILURE_EXECUTE_SPHINX_COMMAND, task.getTaskUID(), e);
            logger.error("Error occurred during agent state execution: " + failureStatus, e);
            task.setStatus(failureStatus);
        }
    }

    protected abstract void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException;

    protected abstract void callback(Status status, Task task);

    protected void callback(Task task) {
        try {
        	getLogger().info("ABOUT TO EXECUTE CALLBACK, TASK STATUS: " + task.getStatus());
        	if (task.getStatus().getCode() == Status.SUCCESS_CODE) {
            	executeWithRetry(Status.build(Status.SystemInterface.COORDINATOR_AGENT, Status.StatusCode.SUCCESS_CODE, task.getTaskUID()), task);
            } else {
            	executeWithRetry(task.getStatus(), task);
            }
        } catch (Throwable e) {
            Status failureStatus = Status.build(Status.SystemInterface.COORDINATOR_CALLBACK, Status.StatusCode.FAILURE_EXECUTE_COORDINATOR_CALLBACK, task.getTaskUID(), e);
            logger.error("Error occurred during agent state callback execution: " + failureStatus, e);
            task.setStatus(failureStatus);
        }
    }
    
    private void executeWithRetry(final Status status, final Task task) throws Throwable {
    	getLogger().info(MessageFormat.format("ABOUT TO EXECUTE CALLBACK, IN: {0}. For task: {1}. Task state: {2}", this, task, task.getState()));
    	retryTemplate.execute(new RetryCallback<Status>() {
    	    public Status doWithRetry(RetryContext context) {    	
    	    	callback(status, task);
    	    	return task.getStatus();
    	    }
    	});
    }
}
