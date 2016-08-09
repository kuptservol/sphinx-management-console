package ru.skuptsov.sphinx.console.coordinator.agent.command;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;

@Component
public class StopSnippetQueryCommandService extends CommandService {
	
	@Resource
    protected ConcurrentHashMap<String, Boolean> snippetsProcessingMap;
	
	@Autowired
	public StopSnippetQueryCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(StopSnippetQueryCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
    	logger.info("ABOUT TO STOP SNIPPET PROCESSING...");
    	//stop snippet processing
    	String taskUID = (String)params[1];
    	
    	snippetsProcessingMap.put(taskUID, false);
    	
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().stopSnippetQueryFinished(status, task);
    }
}
