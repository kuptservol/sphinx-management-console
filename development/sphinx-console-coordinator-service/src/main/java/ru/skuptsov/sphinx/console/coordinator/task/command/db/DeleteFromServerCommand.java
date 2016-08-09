package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.DeleteProcessFromServerTask;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 15.08.14
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
@Component("DELETE_FROM_SERVER")
@Scope("prototype")
public class DeleteFromServerCommand<T extends DeleteProcessFromServerTask> extends DbCommand<T>  {
    @Autowired
    private ServerService serverService;

    @Override
    @SaveActivityLog
    public Status execute(DeleteProcessFromServerTask task) {
        logger.debug("DELETE_FROM_SERVER STATE EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        if(task.getServerId() != null) {
            serverService.deleteServer(task.getServerId());
        }
        
        return status;
    }
}
