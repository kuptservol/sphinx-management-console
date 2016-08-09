package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildSnippetsTask;

@Component("RUN_SNIPPET_QUERY")
@Scope("prototype")
public class RunSnippetQueryCommand<T extends Task> extends AgentCommand<T> implements AsyncCommand  {

    public static final String AGENT_REMOTE_METHOD_NAME = "runSnippetQuery";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(Task task) throws NoSuchMethodException {
    	RebuildSnippetsTask snippetTask = (RebuildSnippetsTask)task;
    	
    	Object[] arguments = {snippetTask, snippetTask.getSnippetConfiguration()};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, SnippetConfiguration.class), arguments);
    }

    @Override
    public String getAgentAddress(Task task){
        return task.getIndexAgentAddress();
    }

}
