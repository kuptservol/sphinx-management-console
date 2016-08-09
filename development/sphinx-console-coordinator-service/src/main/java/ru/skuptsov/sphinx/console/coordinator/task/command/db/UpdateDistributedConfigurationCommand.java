package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.DistributedTask;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationFieldsService;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationService;

@Component("UPDATE_DISTRIBUTED_CONFIGURATION_SEARCH")
@Scope("prototype")
public class UpdateDistributedConfigurationCommand extends DbCommand<DistributedTask> {
	@Autowired
    private ConfigurationService configurationService;
	
	@Autowired
    private ConfigurationFieldsService configurationFieldsService;

	
	@Override
    @SaveActivityLog
    public Status execute(DistributedTask task) {
        logger.debug("UPDATE_CONFIGURATION DISTRIBUTED EXECUTION...");
        
        logger.info("TASK: " + task);

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        Configuration configuration = task.getSearchConfiguration();
        

        if(configuration != null){
        	logger.info("CONFIGURATION, ID: " + configuration.getId());
        	
        	// save configuration
            configurationService.save(configuration);
            
         // save listen port
            Integer port = task.getSearchConfigurationPort();
            ConfigurationFields configurationField = configurationFieldsService.getSearchPort(configuration.getId());
            if(configurationField != null){
                configurationField.setFieldValue(port.toString());
                configurationFieldsService.save(configurationField);
            }
            
        }

        return status;
    }
}
