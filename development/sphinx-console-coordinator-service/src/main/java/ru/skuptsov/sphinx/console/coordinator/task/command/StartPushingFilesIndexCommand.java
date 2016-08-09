package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.agent.service.api.CoordinatorAgentService;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

@Component("START_PUSHING_FILES_INDEX")
@Scope("prototype")
public class StartPushingFilesIndexCommand<T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {

    public static final String AGENT_REMOTE_METHOD_NAME = "startPushingFiles";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(IndexNameTask task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName()};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class), arguments);
    }

    @Override
    public String getAgentAddress(IndexNameTask task){
    	
    	
        return task.getIndexAgentAddress();
    }

}
