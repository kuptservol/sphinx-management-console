package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateSphinxServiceConfService;


@Component("ADD_PROCESS_TO_STARTUP")
@Scope("prototype")
public class AddProcessToStartupCommand<T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {

    public static final String AGENT_REMOTE_METHOD_NAME = "addProcessToStartup";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(IndexNameTask task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName()};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class), arguments);
    }

    @Override
    public String getAgentAddress(IndexNameTask task){
        return task.getSearchAgentAddress();
    }
}
