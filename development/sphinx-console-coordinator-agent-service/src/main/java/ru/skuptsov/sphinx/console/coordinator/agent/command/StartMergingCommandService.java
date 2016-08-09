package ru.skuptsov.sphinx.console.coordinator.agent.command;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.Task.MergeOption;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;

@Component
public class StartMergingCommandService extends CommandService {
	@Autowired
	public StartMergingCommandService(MBeanProxyFactoryBean coordinatorCallbackServiceClient) {
        super(coordinatorCallbackServiceClient);

        logger = LoggerFactory.getLogger(StartMergingCommandService.class);
    }

    @Override
    protected void executeCommands(SphinxService sphinxService, String processName, Object... params) throws IOException {
    	String collectionName = (String)params[1];
        logger.info("COLLECTION NAME: " + collectionName);
        MergeOption mergeOption = (MergeOption)params[2];
        logger.info("COLLECTION NAME: " + collectionName);
        logger.info("MERGE OPTION: " + mergeOption);
        
        String option = " --merge-dst-range deleted 0 0"; // значение по умолчанию

        if (mergeOption != null) {
       		option = " --merge-dst-range " + mergeOption.getFieldName() + " " + mergeOption.getFieldValueFrom() + " " + mergeOption.getFieldValueTo();
        }
        
        if(collectionName != null) {
        	sphinxService.executeStartMergingCommand(processName, collectionName, option);
        }
    }

    @Override
    protected void callback(Status status, Task task) {
        getCoordinatorCallbackServiceClient().startMergingFinished(status, task);
    }
}
