package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 13.08.14
 * Time: 19:51
 * To change this template use File | Settings | File Templates.
 */
@Component("START_DELETING_INDEX_DATA_INDEX")
@Scope("prototype")
public class DeletingIndexDataIndexCommand<T extends Task> extends AgentCommand<T> implements AsyncCommand  {

    public static final String AGENT_REMOTE_METHOD_NAME = "startDeletingIndexData";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(Task task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName()};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class), arguments);
    }

    @Override
    public String getAgentAddress(Task task){
        return task.getIndexAgentAddress();
    }

}
