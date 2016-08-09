package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */

@Component("START_PROCESS")
@Scope("prototype")
public class StartProcessCommand<T extends ProcessTask> extends AgentCommand<T> implements AsyncCommand {
	
    public static final String AGENT_REMOTE_METHOD_NAME = "startProcess";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(ProcessTask task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName()};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class), arguments);
    }

    @Override
    public String getAgentAddress(ProcessTask task){
        return task.getSearchAgentAddress();
    }
}
