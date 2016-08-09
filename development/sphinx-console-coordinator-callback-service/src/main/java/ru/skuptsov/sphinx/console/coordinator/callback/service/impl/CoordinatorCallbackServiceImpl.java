package ru.skuptsov.sphinx.console.coordinator.callback.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.callback.service.api.CoordinatorCallbackService;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.TaskService;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.TaskServiceStrategyFactory;
import ru.skuptsov.sphinx.console.coordinator.task.state.RebuildCollectionState;
import ru.skuptsov.sphinx.console.spring.service.api.ActivityLogService;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ScheduledTaskService;
import ru.skuptsov.sphinx.console.spring.service.api.SnippetConfigurationService;
import ru.skuptsov.sphinx.console.spring.service.impl.TasksMapService;

import java.text.ParseException;
import java.util.Date;


@Component
@ManagedResource(objectName = CoordinatorCallbackServiceImpl.MBEAN_NAME,  description = "") 
public class CoordinatorCallbackServiceImpl implements CoordinatorCallbackService {
	
	private static final Logger logger = LoggerFactory.getLogger(CoordinatorCallbackServiceImpl.class);
	
	public static final String MBEAN_NAME = "coordinator.callback.mbeans:type=config,name=CoordinatorCallbackService";
	
    @Autowired
    protected TaskServiceStrategyFactory<Task> taskServiceStrategyFactory; 

    @Autowired
    private CollectionService collectionService;
    
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    
    
    @Autowired
    private ActivityLogService activityLogService;
    
    @Autowired
    private SnippetConfigurationService snippetConfigurationService;

    @Autowired
    protected TasksMapService tasksMapService;


    @ManagedOperation(description = "")
	public String test() {
        logger.debug("CoordinatorCallbackServiceImpl-" + System.currentTimeMillis());
		
	    return "CoordinatorCallbackServiceImpl-" + System.currentTimeMillis();
	}

	@Override
	public Status processIsDown() {
		// TODO Auto-generated method stub
		return null;
	}

    private TaskService<Task> getTaskService(TaskName taskName) {
    	return taskServiceStrategyFactory.getTaskService(taskName.getTitle());
    }

    private Status buildCallback(Status status, Task task) {
		logger.debug("TASKS MAP: " + tasksMapService.getAllTasks());
		Task concreteTask = (Task) tasksMapService.getTask(task.getTaskUID());
		Task taskForHandleCallback = concreteTask;
        Task taskForHandleCallbackParent = taskForHandleCallback.getParent() != null ? (Task) tasksMapService.getTask(taskForHandleCallback.getParent().getTaskUID()) : null;

        if (taskForHandleCallback != null) {

            try {
                logger.info("STATUS, CODE: " + status.getCode());

                if (status.getCode() == Status.SUCCESS_CODE) {

                    logger.info("CONCRETE TASK, UID: " + taskForHandleCallback.getTaskUID() + ", STATUS: " + taskForHandleCallback.getStatus());
                    logger.info("TASK STATE BEFORE PROCEEDING: " + taskForHandleCallback.getState());

                    if (taskForHandleCallbackParent != null) {
                        if (taskForHandleCallbackParent.getProcessedSubTasks().get() == taskForHandleCallbackParent.getSubTasks().size()) {
                            logger.info("NOTIFY PARENT SUBFLOW HAS BEEN FINISHED, PROCESSED SUBTASKS: " + taskForHandleCallbackParent.getProcessedSubTasks().get());
                            taskForHandleCallbackParent.getProcessedSubTasks().set(0);
                            taskForHandleCallback = taskForHandleCallbackParent;
                        }
                    }
                } else {
                    taskForHandleCallback.setTaskStatus(TaskStatus.FAILURE);
                    taskForHandleCallback.setStatus(status);
                    taskForHandleCallback.getStatus().setStackTrace(status.getStackTrace());
                    
                    if (taskForHandleCallbackParent != null) {
                        taskForHandleCallback = taskForHandleCallbackParent;
                        
                        logger.info("PARENT TASK PROCESSED SUBTASKS: " + taskForHandleCallback.getProcessedSubTasks().get());
                        taskForHandleCallback.getProcessedSubTasks().incrementAndGet();
                        logger.info("PARENT TASK PROCESSED SUBTASKS: " + taskForHandleCallback.getProcessedSubTasks().get());
                    }
	
                    taskForHandleCallback.setStatus(status);
                    taskForHandleCallback.getStatus().setStackTrace(status.getStackTrace());
                    taskForHandleCallback.setTaskStatus(TaskStatus.FAILURE);
                }

                Task taskForLog = taskForHandleCallbackParent != null ? taskForHandleCallbackParent : taskForHandleCallback;
                ActivityLog activityLog = activityLogService.getLast(taskForLog.getTaskUID());
                if (activityLog != null) {
                    activityLogService.buildLogEnd(taskForLog, activityLog);
                    activityLogService.save(activityLog);
                } else { // случай удаления коллекции
                	taskForLog.setShouldBeLogged(false);
                }

                TaskService taskServiceForHandleCallback = getTaskService(taskForHandleCallback.getTaskName());
                taskServiceForHandleCallback.handleAgentCallback(taskForHandleCallback, status);

                return taskForHandleCallback.getStatus();
            } catch (Exception e) {
                logger.error("Error occurred during build callback", e);
                taskForHandleCallback.setTaskStatus(TaskStatus.FAILURE);
                throw new ApplicationException("Error occurred during build callback", e);
            }
		} else {
			logger.error("Can't find proper task for callback handling!");
			throw new ApplicationException("Can't find proper task for callback handling!");
		}
    }

    @Override
	@ManagedOperation(description = "") 
	public Status creatingProcessFinished(Status status, Task task) {
		logger.info("CREATING PROCESS FINISHED, FOR: " + task.getTaskName());

        return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status addProcessToStartupCommandFinished(Status status, Task task) {
		logger.info("ADD TO STARTUP FINISHED FOR PROCESS: " + task.getProcessName());

		return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status removeProcessFromStartupCommandFinished(Status status, Task task) {
		logger.info("REMOVE FROM STARTUP FINISHED FOR PROCESS: " + task.getProcessName());

		return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status indexingFinished(Status status, Task task) {
        logger.info("INDEXING FINISHED: " + task.getTaskUID());
		
        if (status.getCode() == Status.SUCCESS_CODE) {
        	Collection collection = collectionService.getCollection(task.getCollectionName());
        	
        	if (collection != null) {
        		collection.setLastIndexingTime(new Date());
        		
        		ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.INDEXING_DELTA);
        		
        		if (scheduledTask != null) {
        			String cronExpression = scheduledTask.getCronSchedule();
        			logger.info("CRON EXPRESSION: " + cronExpression);
        			try {
						org.quartz.CronExpression parser = new org.quartz.CronExpression(cronExpression);
						
						Date nextIndexingTime = parser.getNextValidTimeAfter(new Date());
						logger.info("NEXT INDEXING TIME: " + nextIndexingTime);
						collection.setNextIndexingTime(nextIndexingTime);
						
					} catch (ParseException e) {
						logger.error("ERROR OCCURED WHILE PARSING CRON EXPRESSION: " + cronExpression, e);
					}
        		}
        		
        		collectionService.save(collection);
        	}
        } else {
        	Task concreteTask = (Task) tasksMapService.getTask(task.getTaskUID());
    		Task taskForHandleCallback = concreteTask;
    		
    		logger.info("TASK FOR HANDLE CALLBACK STATUS: " + taskForHandleCallback.getTaskStatus() + ", " + taskForHandleCallback.getCollectionName() + ", " + taskForHandleCallback.getTaskUID());
			
        	if (taskForHandleCallback.getTaskStatus() == TaskStatus.STOPPED) {
				status = Status.build(Status.SystemInterface.COORDINATOR_AGENT, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
				taskForHandleCallback.setStatus(status);
				taskForHandleCallback.setTaskStatus(TaskStatus.SUCCESS);
				taskForHandleCallback.setState(RebuildCollectionState.COMPLETED);
			}
        }
        
        return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status rotatingFinished(Status status, Task task) {
        logger.info("ROTATING FINISHED, FOR: "+ task.getTaskName());
		
        return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status updatingConfigFinished(Status status, Task task) {
        logger.info("UPDATING CONFIG FINISHED, FOR: "+ task.getTaskName());
		
        return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status pushingFilesFinished(Status status, Task task) {
        logger.info("PUSHING FILES FINISHED, FOR: "+ task.getTaskName());
		
        return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status deletingProcessFinished(Status status, Task task) {
        logger.info("DELETING PROCESS FINISHED, FOR: "+ task.getTaskName());
		
        return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status deletingIndexDataFinished(Status status, Task task) {
        logger.info("DELETING INDEX DATA FINISHED, FOR: "+ task.getTaskName());
		
        return buildCallback(status, task);
	}

    @Override
    @ManagedOperation(description = "")
    public Status deleteIndexDataFilesFinished(Status status, Task task) {
        logger.info("DELETE INDEX DATA FILES FINISHED, FOR: "+ task.getTaskName());

        return buildCallback(status, task);
    }

    @Override
    @ManagedOperation(description = "")
    public Status moveFilesFinished(Status status, Task task) {
        logger.info("MOVE FILES FINISHED, FOR: "+ task.getTaskName());
		
        return buildCallback(status, task);
    }

    @Override
	@ManagedOperation(description = "")
	public Status startProcessFinished(Status status, Task task) {
        logger.info("START PROCESS FINISHED, FOR: "+ task.getTaskName());
		
        return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status stopProcessFinished(Status status, Task task) {
        logger.info("STOP PROCESS FINISHED, FOR: "+ task.getTaskName());
		
        return buildCallback(status, task);
	}

    @Override
	@ManagedOperation(description = "")
	public Status stopIndexingFinished(Status status, Task task) {
        logger.info("STOP INDEXING FINISHED, FOR: "+ task.getTaskName());
		
        return buildCallback(status, task);
    }

	@Override
	@ManagedOperation(description = "")
	public Status startMergingFinished(Status status, Task task) {
        logger.info("START MERGING FINISHED, FOR: "+ task.getTaskName());
        
        if (status.getCode() == Status.SUCCESS_CODE) {
        	Collection collection = collectionService.getCollection(task.getCollectionName());
        	
        	if (collection != null) {
        		collection.setLastMergeTime(new Date());
        		
        		ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.MERGE_DELTA);
        		
        		if (scheduledTask != null) {
        			String cronExpression = scheduledTask.getCronSchedule();
        			logger.info("CRON EXPRESSION: " + cronExpression);
        			try {
						org.quartz.CronExpression parser = new org.quartz.CronExpression(cronExpression);
						
						Date nextMergingTime = parser.getNextValidTimeAfter(new Date());
						logger.info("NEXT MERGING TIME: " + nextMergingTime);
						collection.setNextMergeTime(nextMergingTime);
					} catch (ParseException e) {
						logger.error("ERROR OCCURED WHILE PARSING CRON EXPRESSION: " + cronExpression, e);
					}
        		}
        		
        		collectionService.save(collection);
        	}
        }
		
        return buildCallback(status, task);
	}

	@Override
    @ManagedOperation(description = "")
    public Status deletingSnippetDataFinished(Status status, Task task) {
        logger.info("DELETE SNIPPET DATA FINISHED, FOR: "+ task.getTaskName());

        return buildCallback(status, task);
    }

	@Override
	@ManagedOperation(description = "")
	public Status runSnippetQueryFinished(Status status, Task task) {
		logger.info("RUN SNIPPET QUERY FINISHED, FOR: "+ task.getTaskName());
		
		if (status.getCode() == Status.SUCCESS_CODE) {
        	Collection collection = collectionService.getCollection(task.getCollectionName());
        	
        	if (collection != null) {
        		SnippetConfiguration snippetConfiguration = collection.getSnippetConfiguration();
        		if (snippetConfiguration != null) {
        			snippetConfiguration.setLastBuildSnippet(new Date());
	        		ScheduledTask scheduledTask = scheduledTaskService.findByCollectionName(task.getCollectionName(), ScheduledTaskType.BUILD_SNIPPET);
	        		
	        		if (scheduledTask != null) {
	        			String cronExpression = scheduledTask.getCronSchedule();
	        			logger.info("CRON EXPRESSION: " + cronExpression);
	        			try {
							org.quartz.CronExpression parser = new org.quartz.CronExpression(cronExpression);
							
							Date nextTime = parser.getNextValidTimeAfter(new Date());
							logger.info("NEXT SNIPPET TIME: " + nextTime);
							snippetConfiguration.setNextBuildSnippet(nextTime);
						} catch (ParseException e) {
							logger.error("ERROR OCCURED WHILE PARSING CRON EXPRESSION: " + cronExpression, e);
						}
	        		}
	        		
	        		snippetConfigurationService.save(snippetConfiguration);
        		}
        	}
        }

        return buildCallback(status, task);
	}

	@Override
	@ManagedOperation(description = "")
	public Status runRSyncCommandFinished(Status status, Task task) {
		logger.info("RUN RSYNC COMMAND FINISHED, FOR: "+ task.getTaskName());

        return buildCallback(status, task);
	}
	@Override
	@ManagedOperation(description = "")
	public Status stopSnippetQueryFinished(Status status, Task task) {
		logger.info("STOP SNIPPET QUERY FINISHED, FOR: "+ task.getTaskName());

        return buildCallback(status, task);
	}

}
