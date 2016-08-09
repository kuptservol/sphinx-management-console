package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 13.08.14
 * Time: 22:12
 * To change this template use File | Settings | File Templates.
 */
@Component("START_DELETING_PROCESS")
@Scope("prototype")
public class DeletingProcessCommand<T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {

    public static final String AGENT_REMOTE_METHOD_NAME = "startDeletingProcess";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(IndexNameTask task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName()};

        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class), arguments);
    }

    @Override
    public void executeBeforeAgentRemoteMethod(IndexNameTask task) {
        logger.debug("START_DELETING_PROCESS EXECUTION...");
        task.setSphinxProcessType(SphinxProcessType.SEARCHING);
    }

    @Override
    public String getAgentAddress(IndexNameTask task){
        return task.getSearchAgentAddress();
    }

}