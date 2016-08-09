package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.task.MoveProcessToServerTask;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 26.08.14
 * Time: 2:25
 * To change this template use File | Settings | File Templates.
 */
@Component("MOVE_FILES_INDEX")
@Scope("prototype")
public class MoveFilesIndexCommand extends AgentCommand<MoveProcessToServerTask> implements AsyncCommand {

    @Autowired
    private ServerService serverService;

    public static final String AGENT_REMOTE_METHOD_NAME = "moveFiles";

    Server server;

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(MoveProcessToServerTask task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName(),  server.getIp(), task.getIndexServer().getIp()};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class, String.class, String.class), arguments);
    }

    @Override
    public void executeBeforeAgentRemoteMethod(MoveProcessToServerTask task) {
        logger.debug("START_MOVE_FILES " + task.getSphinxProcessType().name() + " STATE EXECUTION...");

        server = serverService.getServer(task.getOldServerName());

    }

    @Override
    public String getAgentAddress(MoveProcessToServerTask task){
        return task.getIndexAgentAddress();
    }

}

