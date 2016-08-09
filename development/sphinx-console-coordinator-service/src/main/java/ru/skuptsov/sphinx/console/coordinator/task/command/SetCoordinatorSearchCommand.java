package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.MoveProcessToServerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
@Component("SET_COORDINATOR_SEARCH")
@Scope("prototype")
public class SetCoordinatorSearchCommand<T extends Task> extends AgentCommand<T> implements SyncCommand {

    public static final String AGENT_REMOTE_METHOD_NAME = "setCoordinator";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(Task task) throws NoSuchMethodException {
        Object[] arguments = {task};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class), arguments);
    }

    @Override
    public String getAgentAddress(Task task){
        return task.getSearchAgentAddress();
    }

}
