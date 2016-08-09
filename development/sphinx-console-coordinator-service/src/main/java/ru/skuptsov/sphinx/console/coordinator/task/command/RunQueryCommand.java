package ru.skuptsov.sphinx.console.coordinator.task.command;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.monitoring.MonitoringService;
import ru.skuptsov.sphinx.console.coordinator.task.command.db.CoordinatorCommand;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationFieldsService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

@Component("RUN_QUERY")
@Scope("prototype")
public class RunQueryCommand<T extends Task> extends CoordinatorCommand<T> implements SyncCommand {
		
	@Autowired
    protected ProcessService processService;
	
	@Autowired
    protected ConfigurationFieldsService configurationFieldsService;

    @Autowired
    protected ServerService serverService;

    @Override
    @SaveActivityLog
    public Status execute(final Task task) {
        logger.debug("START RUN QUERY...");
        
        SphinxProcess searchSphinxProcess = processService.findSearchProcess(task.getCollectionName(), task.getReplicaNumber());
        Server server = searchSphinxProcess.getServer(); 
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
        
        MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
        
        Integer port = Integer.valueOf(searchSphinxProcess.getConfiguration().getSearchListenPort());

        try {
           Boolean result = monitoringService.runQuery(server.getIp(), adminProcess.getPort(), port, task.getCollectionName());
           logger.debug("RUN QUERY RESULT: " + result);
           if (result != null && result) {
        	   logger.debug("RUN QUERY SUCCESS...");
        	   return Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
           }
        } catch(Throwable e) {
            logger.error(e.getMessage(), e);
            return Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.FAILURE_EXECUTE_SPHINX_COMMAND, task.getTaskUID(), e);
        }

        return Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.FAILURE_EXECUTE_SPHINX_COMMAND, task.getTaskUID());
    }

}
