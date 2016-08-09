package ru.skuptsov.sphinx.console.coordinator.task.schedule.service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.monitoring.MonitoringService;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.RebuildCollectionTaskService;
import ru.skuptsov.sphinx.console.spring.service.impl.TasksMapService;
import ru.skuptsov.sphinx.console.coordinator.util.DeepCopyService;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

@Service("rebuildCollectionScheduleService")
@Scope("prototype")
public class RebuildCollectionScheduleService extends AbstractScheduleService {
	private static final Logger logger = LoggerFactory.getLogger(RebuildCollectionScheduleService.class);
	
	public static final Long FIRST_REPLICA = 1L; 
	
	@Autowired
	private RebuildCollectionTaskService rebuildCollectionTaskService;
	
	@Autowired
	private ServerService serverService;
	
	@Autowired
	private ProcessService processService;

	@Autowired
	protected CollectionService collectionService;
	
	@Autowired
	DeepCopyService deepCopyService;

    @Autowired
    private TasksMapService tasksMapService;

    protected Status createSubTasks(Task task, List<Replica> replicas, Class<? extends Task> T) {
		List<Task> subTasks = new ArrayList<Task>();
		
		for (Replica replica : replicas) {
		    Task subTask = deepCopyService.deepCopy(task, T);
		    subTask.setState(task.getSubflowChain().getFirstState());
		    subTask.setParent(task);
		    
		    subTask.setIndexAgentAddress(task.getIndexAgentAddress());
        	subTask.setIndexServer(task.getIndexServer());
        	subTask.setCoordinatorAddress(task.getCoordinatorAddress());
        	subTask.setStatus(Status.build(SystemInterface.COORDINATOR_AGENT, StatusCode.SUCCESS_CODE, task.getTaskUID()));
		    
		    subTask.setReplicaNumber(replica.getNumber());
		    subTask.setTaskUID(UUID.randomUUID().toString());
		    subTask.setParentTaskUID(task.getTaskUID());
		    SphinxProcess searchSphinxProcess = processService.findSearchProcess(task.getCollectionName(), replica.getNumber());
		    if (searchSphinxProcess != null) {
    			Server server = searchSphinxProcess.getServer();
    			if (server != null) {
    				subTask.setSearchServer(server);
    				AdminProcess searchAdminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
    				if (searchAdminProcess != null) {
						jmxService.setTaskAgentAddress(subTask, searchAdminProcess);
    				}
    			}
		    }
            Status putTaskStatus = tasksMapService.putTask(subTask);
            if (putTaskStatus.getCode() != Status.SUCCESS_CODE) {
                return putTaskStatus;
            } else {
                subTasks.add(subTask);
            }
        }

        task.setSubTasks(subTasks);

        return Status.build(SystemInterface.COORDINATOR_CONFIGURATION, StatusCode.SUCCESS_CODE);
    }

    protected List<Replica> getReplicas(String collectionName) {
	    Set<Replica> replicas = collectionService.getCollection(collectionName).getReplicas();
	    if (replicas != null) {
	        return new LinkedList<Replica>(replicas);
	    }
	    return null;
    }
	
	public void execute() {
		String collectionName = getCollectionName();
    	logger.info("EXECUTION IN REBUILD COLLECTION SCHEDULE SERVICE, INSTANCE: " + this + ", FOR: " + collectionName);
    	
    	MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
    	
    	if (!monitoringService.canExecuteIndexing(collectionName, FIRST_REPLICA)) {
    		logger.info("INDEXING HAS NOT BEEN EXECUTED...");
    		return;
    	}
    	
    	RebuildCollectionTask task = new RebuildCollectionTask();
    	task.setCollectionName(collectionName);
    	task.setReplicaNumber(FIRST_REPLICA);
		jmxService.setTaskCoordinatorAddress(task);

    	SphinxProcess searchSphinxProcess = processService.findSearchProcesses(task.getCollectionName()).get(0); //TODO
		
		if (searchSphinxProcess != null) {
			Server server = searchSphinxProcess.getServer();
			
			if (server != null) {
				AdminProcess searchAdminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
				if (searchAdminProcess != null) {
					jmxService.setTaskAgentAddress(task, searchAdminProcess);
	    		    task.setSearchServer(server);
				}
			}
			
			
		}
		
        SphinxProcess indexSphinxProcess = processService.findIndexingProcess(task.getCollectionName()); //TODO
		
		if (indexSphinxProcess != null) {
			Server server = indexSphinxProcess.getServer();
			
			if (server != null) {
				AdminProcess indexAdminProcess = serverService.getAdminProcess(ProcessType.INDEX_AGENT, server);
				if (indexAdminProcess != null) {
					jmxService.setTaskAgentAddress(task, indexAdminProcess);
	    		    task.setIndexServer(server);
				}
			}
			
			
		}

        Status createSubTasksStatus = createSubTasks(task, getReplicas(task.getCollectionName()), task.getClass());
        if (createSubTasksStatus.getCode() == Status.SUCCESS_CODE) {
            logger.info("TASK STATE: " + task.getState());
            logger.info("TASK UID: " + task.getTaskUID());

            Status putTaskStatus = tasksMapService.putTask(task);

            if (putTaskStatus.getCode() == Status.SUCCESS_CODE) {
                task.setStartDate(new Date());
                Status status = rebuildCollectionTaskService.execute(task);
                logger.info("STATUS: " + status);
                task.setStatus(status);
            } else {
                logger.warn("Can't start scheduled task, because can't add task to global tasks: " + putTaskStatus);
            }
        } else {
            logger.warn("Can't start scheduled task, because can't add sub tasks to global tasks: " + createSubTasksStatus);
        }

        logger.info("TASK STATE: " + task.getState());

    }
}
