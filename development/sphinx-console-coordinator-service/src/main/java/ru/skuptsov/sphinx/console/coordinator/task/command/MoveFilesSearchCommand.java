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
 * Date: 19.08.14
 * Time: 0:25
 * To change this template use File | Settings | File Templates.
 */
@Component("MOVE_FILES_SEARCH")
@Scope("prototype")
public class MoveFilesSearchCommand extends AgentCommand<MoveProcessToServerTask> implements AsyncCommand {
    
    @Autowired
    private ServerService serverService;

    public static final String AGENT_REMOTE_METHOD_NAME = "moveFiles";

    Server server;

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(MoveProcessToServerTask task) throws NoSuchMethodException {
        Object[] arguments = {task, task.getProcessName(),  server.getIp(), task.getSearchServer().getIp()};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class, String.class, String.class, String.class), arguments);
    }

    @Override
    public void executeBeforeAgentRemoteMethod(MoveProcessToServerTask task) {
        logger.debug("START_MOVE_FILES " + task.getSphinxProcessType().name() + " STATE EXECUTION...");

        server = serverService.getServer(task.getOldServerName());
    }

    @Override
    public String getAgentAddress(MoveProcessToServerTask task){
        return task.getSearchAgentAddress();
    }

}
