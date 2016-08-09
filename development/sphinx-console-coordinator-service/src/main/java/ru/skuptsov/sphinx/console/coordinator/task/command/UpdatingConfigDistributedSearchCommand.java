package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateDistributedSphinxConfService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;

@Component("START_UPDATING_SPHINX_CONFIG_DISTRIBUTED_SEARCH")
@Scope("prototype")
public class UpdatingConfigDistributedSearchCommand<T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {
	 
    @Autowired
    private GenerateDistributedSphinxConfService generateDistributedSphinxConfService;
    @Autowired
    private ProcessService processService;

    public static final String AGENT_REMOTE_METHOD_NAME = "startUpdatingConfig";

    String sphinxConfContent;

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(IndexNameTask task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName(), sphinxConfContent};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class, String.class), arguments);
    }

    @Override
    public void executeBeforeAgentRemoteMethod(IndexNameTask task) {
        logger.debug("START_UPDATING_SPHINX_CONFIG_DISTRIBUTED_SEARCH EXECUTION...");
        if(task.getSearchConfiguration() == null) { 
            SphinxProcess searchSphinxProcess = processService.findSearchProcess(task.getCollectionName(), task.getReplicaNumber());
            task.setSearchConfiguration(searchSphinxProcess.getConfiguration());
            task.setSearchConfigurationPort(Integer.parseInt(searchSphinxProcess.getConfiguration().getSearchListenPort()));
        }
        sphinxConfContent = generateDistributedSphinxConfService.generateContent(task);
    }

    @Override
    public String getAgentAddress(IndexNameTask task){
        return task.getSearchAgentAddress();
    }
}

