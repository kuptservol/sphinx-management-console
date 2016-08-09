package ru.skuptsov.sphinx.console.coordinator.agent.command;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 11.08.14
 * Time: 22:57
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MoveFilesCommandService extends CommandService {
    @Autowired
    public MoveFilesCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(CopyFilesCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
        String fromServer = (String)params[1];
        String toServer   = (String)params[2];
        if(fromServer != null && toServer != null) {
        	sphinxService.copyAndRenameFiles(processName, fromServer, toServer);
        }
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().moveFilesFinished(status, task);
    }
}
