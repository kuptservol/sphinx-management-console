package ru.skuptsov.sphinx.console.coordinator.agent.command;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.IndexType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 11.08.14
 * Time: 1:43
 * To change this template use File | Settings | File Templates.
 */

@Component
public class CopyFilesCommandService extends CommandService {
	@Autowired
	public CopyFilesCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(CopyFilesCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
        String indexFileName = (String) params[1];
        String fromServer = (String) params[2];
        String toServer = (String) params[3];
        String fromDir = (String) params[4];
        String toDir = (String) params[5];
        String indexType = ((IndexType) params[6]).getTitle();
        Boolean isStrictCopy = (Boolean) params[7];

        sphinxService.copyIndexFiles(indexFileName, fromServer, toServer, fromDir, toDir, indexType, isStrictCopy);
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().pushingFilesFinished(status, task);
    }
}
