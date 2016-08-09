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
 * Time: 0:49
 * To change this template use File | Settings | File Templates.
 */
@Component
public class DeleteProcessCommandService extends CommandService {
    
	@Autowired
	public DeleteProcessCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(DeleteProcessCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
    	sphinxService.deleteProcessFile(processName);
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().deletingProcessFinished(status, task);
    }
}
