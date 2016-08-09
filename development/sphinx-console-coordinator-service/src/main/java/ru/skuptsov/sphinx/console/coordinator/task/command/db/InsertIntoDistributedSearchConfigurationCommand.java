package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationFieldsService;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationService;

@Component("INSERT_INTO_CONFIGURATION_DISTRIBUTED_SEARCH")
@Scope("prototype")
public class InsertIntoDistributedSearchConfigurationCommand extends DbCommand<ProcessTask> {
	@Autowired
	private ConfigurationService configurationService;
    @Autowired
    private ConfigurationFieldsService configurationFieldsService;

    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        logger.debug("INSERT_INTO_CONFIGURATION_SEARCH_DISTRIBUTED EXECUTION...");
        
        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());
        
        Configuration configuration = configurationService.getConfiguration(task.getSearchConfigurationName());
        logger.info("CONFIGURATION: " + configuration);
        if(configuration == null) {
            configuration = task.getSearchConfiguration();
            logger.info("CONFIGURATION: " + configuration.getName());
        }
        
        configuration = configurationService.save(configuration);
        
        task.setSearchConfiguration(configurationService.findById(configuration.getId(), "sourceConfigurationFields", "configurationTemplate", "configurationTemplate.configurationFields",
        		"searchConfigurationTemplate", "searchConfigurationTemplate.configurationFields"));

        // save listen port
        Integer port = task.getSearchConfigurationPort();
        ConfigurationFields configurationField = new ConfigurationFields();
        configurationField.setFieldKey("listen");
        configurationField.setFieldValue(port.toString());
        configurationField.setConfigurationType(ConfigurationType.SEARCH);
        configurationField.setConfiguration(configuration);
        configurationFieldsService.save(configurationField);

        
        return status;
    }
}
