package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.AdminProcess;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessStatus;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.monitoring.MonitoringService;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

@Component("START_ROTATING_SEARCH")
@Scope("prototype")
public class StartRotatingSearchCommand<T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {

    public static final String AGENT_REMOTE_METHOD_NAME = "startRotating";
    
    @Autowired
    private ProcessService processService;

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(IndexNameTask task) throws NoSuchMethodException {
    	Object[] arguments = {task, task.getProcessName()};
        
    	MonitoringService monitoringService = ApplicationContextProvider.getBean("monitoringService");
    	
    	SphinxProcess searchSphinxProcess = processService.findSearchProcess(task.getCollectionName(), task.getReplicaNumber());
        
    	Server server = searchSphinxProcess.getServer();
        AdminProcess adminProcess = serverService.getAdminProcess(ProcessType.SEARCH_AGENT, server);
        String processName = task.getCollectionName() + "_" + task.getReplicaNumber();
        ProcessStatus processStatus = monitoringService.isProcessAlive(server.getIp(), adminProcess.getPort(), processName);
        logger.info("PROCESS STATUS: " + processStatus + ", PROCESS NAME: " + processName);
        if (processStatus == ProcessStatus.FAILURE) {
        	 task.setShouldBeLogged(false);
        	 return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class), arguments, false); // cannot be executed	
        }
    	
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class), arguments);
    }

    @Override
    public String getAgentAddress(IndexNameTask task){
        return task.getSearchAgentAddress();
    }

}
