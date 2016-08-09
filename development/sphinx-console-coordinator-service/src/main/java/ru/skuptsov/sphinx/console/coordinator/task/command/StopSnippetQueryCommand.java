package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.Task;

@Component("STOP_SNIPPET_QUERY")
@Scope("prototype")
public class StopSnippetQueryCommand<T extends Task> extends AgentCommand<T> implements AsyncCommand  {

    public static final String AGENT_REMOTE_METHOD_NAME = "stopSnippetQuery";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(Task task) throws NoSuchMethodException {
    	Object[] arguments = {task};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class), arguments);
    }

    @Override
    public String getAgentAddress(Task task){
        return task.getIndexAgentAddress();
    }

}
