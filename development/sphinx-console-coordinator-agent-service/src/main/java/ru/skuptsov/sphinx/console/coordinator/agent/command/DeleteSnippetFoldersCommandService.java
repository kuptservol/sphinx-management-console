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
public class DeleteSnippetFoldersCommandService extends CommandService {
    
	@Autowired
	public DeleteSnippetFoldersCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(DeleteSnippetFoldersCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
    	sphinxService.deleteSnippetFolders(processName);
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().deletingSnippetDataFinished(status, task);
    }
}
