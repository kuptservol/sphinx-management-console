package ru.skuptsov.sphinx.console.coordinator.agent.command;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;

@Component
public class StopIndexingCommandService extends CommandService {
	
	@Autowired
	public StopIndexingCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(StopIndexingCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
    	sphinxService.executeStopIndexerCommand(processName);
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().stopIndexingFinished(status, task);
    }
}
