package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;

@Component("START_DELETING_INDEX_DATA_FILES")
@Scope("prototype")
public class DeletingIndexDataFilesCommand<T extends Task> extends AgentCommand<T> implements AsyncCommand  {

    public static final String AGENT_REMOTE_METHOD_NAME = "startDeleteIndexDataFiles";

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
