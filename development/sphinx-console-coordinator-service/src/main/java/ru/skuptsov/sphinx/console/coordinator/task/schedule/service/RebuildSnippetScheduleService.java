package ru.skuptsov.sphinx.console.coordinator.task.schedule.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.monitoring.MonitoringService;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildSnippetsTask;
import ru.skuptsov.sphinx.console.coordinator.task.service.api.RebuildSnippetsTaskService;
import ru.skuptsov.sphinx.console.coordinator.util.DeepCopyService;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;
import ru.skuptsov.sphinx.console.spring.service.impl.TasksMapService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

@Service("rebuildSnippetScheduleService")
@Scope("prototype")
public class RebuildSnippetScheduleService extends AbstractScheduleService {
	private static final Logger logger = LoggerFactory.getLogger(RebuildSnippetScheduleService.class);
	
	public static final Long FIRST_REPLICA = 1L; 
	
	@Autowired
	private RebuildSnippetsTaskService rebuildSnippetsTaskService;
	
	@Autowired
	private ServerService serverService;
	
	@Autowired
	private ProcessService processService;

	@Autowired
	protected CollectionService collectionService;

    @Autowired
    protected TasksMapService tasksMapService;

    @Autowired
	DeepCopyService deepCopyService;
	
	public void execute() {
		String collectionName = getCollectionName();
    	logger.info("EXECUTION IN REBUILD SNIPPET SCHEDULE SERVICE, INSTANCE: " + this + ", FOR: " + collectionName);
       
        Collection collection = collectionService.getCollection(collectionName);
		
		MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");

		if (monitoringService.isCurrentlyRebuildSnippet(collectionName)) {
			logger.info("REBUILD SNIPPET HAS NOT BEEN EXECUTED...");
			return;
		}
		
		RebuildSnippetsTask task = new RebuildSnippetsTask();
    	task.setCollectionName(collectionName);
    	task.setReplicaNumber(FIRST_REPLICA);
    	task.setSnippetConfiguration(collection.getSnippetConfiguration());

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
            Status status = rebuildSnippetsTaskService.execute(task);
            logger.info("STATUS: " + status);
            task.setStatus(status);
        }
        else{
            logger.warn("Can't start scheduled task, because can't add task to global tasks: " + putTaskStatus);
        }

		logger.info("TASK STATE: " + task.getState());
	}
}
