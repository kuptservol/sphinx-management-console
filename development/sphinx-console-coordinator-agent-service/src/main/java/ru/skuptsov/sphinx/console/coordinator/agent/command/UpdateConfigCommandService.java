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
 * Time: 0:14
 * To change this template use File | Settings | File Templates.
 */
@Component
public class UpdateConfigCommandService extends CommandService {
    
	@Autowired
	public UpdateConfigCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(UpdateConfigCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
        String content = (String)params[1];
        logger.info("SPHINX CONFIG CONTENT: " + content);
        if(content != null) {
        	sphinxService.updateConfig(processName, content);
        }
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().updatingConfigFinished(status, task);
    }
}
