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
public class RunRSyncCommandService extends CommandService {
	@Autowired
	public RunRSyncCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(RunRSyncCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
    	logger.info("ABOUT TO START RUN RSYNC COMMAND...");
    	
        String fromServer = (String)params[1];
        String toServer   = (String)params[2];
        String fromDir = (String) params[3];
        String toDir = (String) params[4];
         
        logger.info("FROM SERVER: " + fromServer);
        logger.info("TO SERVER: " + toServer);
        logger.info("PROCESS NAME: " + processName);
        logger.info("FROM DIR: " + fromDir);
        logger.info("TO DIR: " + toDir);
         
        if(fromServer != null && toServer != null) {
            logger.info("EXECUTE, snippetRSync: " + sphinxService);
        	sphinxService.snippetRSync(processName, fromServer, toServer, fromDir, toDir);
        }
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().runRSyncCommandFinished(status, task);
    }
}