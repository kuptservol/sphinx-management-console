package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.model.ActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.TaskService;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.TaskServiceStrategyFactory;
import ru.skuptsov.sphinx.console.coordinator.task.state.TaskState;
import ru.skuptsov.sphinx.console.spring.service.api.ActivityLogService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 20:59
 * To change this template use File | Settings | File Templates.
 */
public abstract class AgentCommand<T extends Task> extends Command<T> {
    protected static final Logger logger = LoggerFactory.getLogger(AgentCommand.class);
    protected static final Class AGENT_REMOTE_CLASS = CoordinatorAgentService.class;
    private static final int ALLOWED_CONNECTIONS_IN_POOL_FOR_PARTICULAR_AGENT = 1;
    
    public static final long HOUR_1 = 3600000; 

    public abstract AgentRemoteMethod getAgentRemoteMethod(T task) throws NoSuchMethodException;
    public abstract String getAgentAddress(T task);

    @Autowired
    private RetryTemplate retryTemplate;
    
    @Autowired
    protected ServerService serverService;
    
    @Autowired
    protected TaskServiceStrategyFactory<Task> taskServiceStrategyFactory; 


    private org.springframework.jmx.access.MBeanProxyFactoryBean coordinatorAgentServiceClient;
    private CoordinatorAgentService coordinatorAgentService;
    
    @Autowired
    @Qualifier("agentClientsPool")
    protected GenericKeyedObjectPool<String, org.springframework.jmx.access.MBeanProxyFactoryBean> agentClientsPool;
    
    @Autowired
	private TaskExecutor executor; 
    
    @Autowired
    private ActivityLogService activityLogService;
    
    private TaskService<Task> getTaskService(TaskName taskName) {
    	return taskServiceStrategyFactory.getTaskService(taskName.getTitle());
    }

    @Override
    public Status.StatusCode getFailureCode() {
        return Status.StatusCode.FAILURE_EXECUTE_SPHINX_COMMAND;
    }

    @Override
    public Status.SystemInterface getSystemInterface() {
        return Status.SystemInterface.COORDINATOR_AGENT;
    }

    public void setAgent(T task) throws Exception {
        logger.info("Set service url for agent command: " + getAgentAddress(task));
        coordinatorAgentServiceClient.setServiceUrl(getAgentAddress(task));
        coordinatorAgentService = (CoordinatorAgentService)coordinatorAgentServiceClient.getObject();
    }

    private void getConnectionFromPool(String serviceUrl) {
    	
    	
    	long active = agentClientsPool.getNumActive(serviceUrl);
    	long idle = agentClientsPool.getNumIdle(serviceUrl);
    	
    	logger.debug("NUM ACTIVE: " + active);
    	logger.debug("NUM IDLE: " + idle);
    	
    
    	try {
    			
    		coordinatorAgentServiceClient = (MBeanProxyFactoryBean) agentClientsPool.borrowObject(serviceUrl);	
        	
    		active = agentClientsPool.getNumActive(serviceUrl);
    		
    		logger.debug("NUM ACTIVE AFTER BORROW: " + active);
    		
    		if (active >= ALLOWED_CONNECTIONS_IN_POOL_FOR_PARTICULAR_AGENT) {
    			agentClientsPool.returnObject(serviceUrl, coordinatorAgentServiceClient);		
    		}
    		
    		logger.debug("INSTANCE OF coordinatorAgentServiceClient: " + coordinatorAgentServiceClient);
    		
		} catch (Exception e) {
            logger.error("Error during getConnectionFromPool for agent command", e);
			throw new ApplicationException(e);
		}
    }
    
    
    @Override
    @SaveActivityLog
    public Status execute(T task) {        
        Status status = null;
        String serviceUrl = "";
            
        serviceUrl = getAgentAddress(task);
        
        logger.debug("ABOUT TO GET AGENT CONNECTION, FOR: " + serviceUrl);
        
        	
        getConnectionFromPool(serviceUrl);


        try {
            // код перед выполнением удаленного метода
            executeBeforeAgentRemoteMethod(task);
            // проверка валидности и выполнение удаленного метода
            AgentRemoteMethod agentRemoteMethod = getAgentRemoteMethod(task);
            
            if (!agentRemoteMethod.canBeExecuted()) {
                status = Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE);
                
                TaskService taskServiceForHandleCallback = getTaskService(task.getTaskName());
    			taskServiceForHandleCallback.handleAgentCallback(task, status);
    			return status;
            }
            
            if (validateRemoteAgentMethod(agentRemoteMethod)) {
                status = executeWithRetry(agentRemoteMethod);
            } else {
                throw new ApplicationException("Удаленный метод агента задан некорректно.");
            }
        } catch (Throwable e) {
            logger.error("ERROR OCCURED WHILE EXECUTING REMOTE COMMAND: " + e);
            throw new ApplicationException(e);
        }
        return status;

    }
    
   
    
    private Status executeWithRetry(final AgentRemoteMethod agentRemoteMethod) throws Throwable {
        Status result = retryTemplate.execute(new RetryCallback<Status>() {
	    	    public Status doWithRetry(RetryContext context) throws InvocationTargetException, IllegalAccessException {
	    	    	try {
	    	    	    setAgent((T)(agentRemoteMethod.getArguments()[0]));
	    	    	} catch (Throwable e) {
	    	    		logger.error("ERROR OCCURED WHILE SETTING AGENT ADDRESS: " + e);
	    	    		throw new ApplicationException(e);
	    	    	}
	    	    	
	    	    	
	    	    	final Task task = (Task)agentRemoteMethod.getArguments()[0];
                    final TaskState currentState = task.getState();
                    
                    executor.execute(new Runnable() {
                        public void run() {
                        	long startTime = System.currentTimeMillis();
                            long inProcessingTime = System.currentTimeMillis();
                        	while ((task.getState()  == currentState && task.getTaskStatus() != TaskStatus.FAILURE) || inProcessingTime - startTime > HOUR_1) {
                        		logger.info("ABOUT TO HEALTH CHECK AGENT, FOR TASK EXECUTION: " + task);
                            	try { 
                            		String serviceUrl = getAgentAddress((T)(agentRemoteMethod.getArguments()[0]));
                            	    getConnectionFromPool(serviceUrl);
                            		
                            		CoordinatorAgentService coordinatorAgentService =  (CoordinatorAgentService) coordinatorAgentServiceClient.getObject();
                            		coordinatorAgentService.test();
                            	    
                            	} catch (Throwable e) {
                            		 logger.error("AGET IS UNAVAILABLE: ", e);
                            		 logger.info("TASK: " + task + ", STATE: " + task.getState());
                            		 task.setTaskStatus(TaskStatus.FAILURE);
                            		 
                            		 Status status = Status.build(Status.SystemInterface.COORDINATOR_CALLBACK, Status.StatusCode.AGENT_SERVICE_UNAVAILABLE, task.getTaskUID(), e);
                            		 task.setStatus(status);
                            		 logger.info("ABOUT TO FAILURE TASK, UID: " + task.getTaskUID());
                            			
                            		 ActivityLog activityLog = activityLogService.getLast(task.getTaskUID());
                            			
                            		 logger.info("RETRIVED ACTIVITY LOG: " + activityLog);
                            			
                            		 if (activityLog != null) {
                            			 activityLogService.buildLogEnd(task, activityLog);
                                         activityLogService.save(activityLog);
                            		 }
                                     TaskService taskServiceForHandleCallback = getTaskService(task.getTaskName());
                         			 taskServiceForHandleCallback.handleAgentCallback(task, task.getStatus());
                         			 return; 
                    	    	}
                            	
                            	try {
									Thread.sleep(30000);
								} catch (InterruptedException e) {
                                    logger.error("Interrupted", e);
								}
                            	
                            	inProcessingTime = System.currentTimeMillis();
                        	}
                        }
                    });
	    	    	
                    return (Status) agentRemoteMethod.getMethod().invoke(coordinatorAgentService, agentRemoteMethod.getArguments());
	    	    }
	    	});
    	
	    return result;
		
    }

    private boolean validateRemoteAgentMethod(AgentRemoteMethod agentRemoteMethod){
        return agentRemoteMethod != null && Arrays.asList(AGENT_REMOTE_CLASS.getMethods()).contains(agentRemoteMethod.getMethod());

    }

    public void executeBeforeAgentRemoteMethod(T task) {
        String commandName = this.getClass().getAnnotation(Component.class).value();
        logger.debug(MessageFormat.format("{0} state execution for sphinx process type {1}.", commandName, task.getSphinxProcessType()));
    }

}
