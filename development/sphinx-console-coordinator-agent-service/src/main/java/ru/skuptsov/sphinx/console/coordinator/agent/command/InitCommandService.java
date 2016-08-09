package ru.skuptsov.sphinx.console.coordinator.agent.command;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;

import java.io.IOException;


@Component
public class InitCommandService extends CommandService {
	@Autowired
	protected InitCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(InitCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
        logger.info("InitCommandService start.");
        logger.info("sphinxService class - " + (sphinxService != null ? sphinxService.getClass().getName() : "null"));
    	String sphinxServiceContent = (String)params[1];
        logger.info("InitCommandService: createProcessFile run. sphinxServiceContent - " + sphinxServiceContent);
    	sphinxService.createProcessFile(processName, sphinxServiceContent);
        logger.info("InitCommandService: createSphinxFolders run. processName - " + processName);
    	sphinxService.createSphinxFolders(processName);
        logger.info("InitCommandService end.");
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().creatingProcessFinished(status, task);
    }
}
