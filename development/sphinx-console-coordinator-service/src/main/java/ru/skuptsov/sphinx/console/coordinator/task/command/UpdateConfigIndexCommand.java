package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateSphinxConfService;
import ru.skuptsov.sphinx.console.coordinator.model.Task;


@Component("START_UPDATING_SPHINX_CONFIG_INDEX")
@Scope("prototype")
public class UpdateConfigIndexCommand<T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {
	 
	@Autowired
    private GenerateSphinxConfService generateSphinxConfService;

    public static final String AGENT_REMOTE_METHOD_NAME = "startUpdatingConfig";

    String sphinxConfContent;

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(IndexNameTask task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName(), sphinxConfContent};

        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class, String.class), arguments);
    }

    @Override
    public void executeBeforeAgentRemoteMethod(IndexNameTask task) {
        logger.debug("START_UPDATING_SPHINX_CONFIG_" + task.getSphinxProcessType().name() + " EXECUTION...");

        sphinxConfContent = generateSphinxConfService.generateContent(task, task.getSphinxProcessType());

    }

    @Override
    public String getAgentAddress(IndexNameTask task){
        return task.getIndexAgentAddress();
    }
}
