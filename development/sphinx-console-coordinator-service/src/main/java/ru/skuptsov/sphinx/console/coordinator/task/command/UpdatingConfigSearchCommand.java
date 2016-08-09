package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateSphinxConfService;


/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 21:28
 * To change this template use File | Settings | File Templates.
 */
@Component("START_UPDATING_SPHINX_CONFIG_SEARCH")
@Scope("prototype")
public class UpdatingConfigSearchCommand<T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {
	 
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
        return task.getSearchAgentAddress();
    }
}
