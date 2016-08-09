package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

@Component("STOP_PROCESS_IGNORE_FAILURE")
@Scope("prototype")
public class StopProcessIgnoreFailureCommand<T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {
    
    public static final String AGENT_REMOTE_METHOD_NAME = "stopProcessIgnoreFailure";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(IndexNameTask task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName()};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class), arguments);
    }

    /*Try to stop sphinx service but ignore failure. This command should be used only during collection delete task,
    because files and directories can be in inconsistent state*/
    @Override
    public void executeBeforeAgentRemoteMethod(IndexNameTask task) {
        logger.debug("STOP_PROCESS_IGNORE_FAILURE EXECUTION...");
        task.setSphinxProcessType(SphinxProcessType.SEARCHING);

    }

    @Override
    public String getAgentAddress(IndexNameTask task){
        return task.getSearchAgentAddress();
    }
}
