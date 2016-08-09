package ru.skuptsov.sphinx.console.coordinator.task.schedule.service;

import java.util.Date;
import java.util.List;

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
import ru.skuptsov.sphinx.console.coordinator.monitoring.MonitoringService;
import ru.skuptsov.sphinx.console.coordinator.task.MergeCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.MergeCollectionTaskService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ReplicaService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;
import ru.skuptsov.sphinx.console.spring.service.impl.TasksMapService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

@Service("mergeCollectionScheduleService")
@Scope("prototype")
public class MergeCollectionScheduleService extends AbstractScheduleService {
private static final Logger logger = LoggerFactory.getLogger(MergeCollectionScheduleService.class);
	
	@Autowired
	private MergeCollectionTaskService mergeCollectionTaskService;
	
	@Autowired
	private ServerService serverService;
	
	@Autowired
	private ProcessService processService;
	
	@Autowired
    private ReplicaService replicaService;

    @Autowired
    protected TasksMapService tasksMapService;

    private void initReplicaSearchAgent(Replica replica) {
        SphinxProcess searchSphinxProcess = processService.findSearchProcess(replica.getCollection().getName(), replica.getNumber());
        if (searchSphinxProcess != null) {
            Server server = searchSphinxProcess.getServer();
            if (server != null) {
                replica.setSearchProcess(searchSphinxProcess);
                AdminProcess searchAdminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
                if (searchAdminProcess != null) {
					jmxService.setReplicaAgentAddress(replica, searchAdminProcess);
                }
            }
        }
    }
	
	private List<Replica> initReplicas(String collectionName) {
	        List<Replica> replicas = replicaService.findByCollectionName(collectionName);
	        for (Replica replica : replicas) {
	            initReplicaSearchAgent(replica);
	        }

	        return replicas;
	}

	public void execute() {
		String collectionName = getCollectionName();
    	logger.info("EXECUTION IN MERGE COLLECTION SCHEDULE SERVICE, INSTANCE: " + this + ", FOR: " + collectionName);
    	
    	MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
    	
    	
    	
    	MergeCollectionTask task = new MergeCollectionTask();
    	task.setCollectionName(collectionName);
    	task.initReplicaLoop(initReplicas(collectionName));
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

        logger.info("TASK STATE: " + task.getState());
        logger.info("TASK UID: " + task.getTaskUID());

        Status putTaskStatus = tasksMapService.putTask(task);

        if(putTaskStatus.getCode() == Status.SUCCESS_CODE){
            task.setStartDate(new Date());
            Status status = mergeCollectionTaskService.execute(task);
            logger.info("STATUS: " + status);
            task.setStatus(status);
        }
        else{
            logger.warn("Can't start scheduled task, because can't add task to global tasks: " + putTaskStatus);
        }

		logger.info("TASK STATE: " + task.getState());

    }
}
