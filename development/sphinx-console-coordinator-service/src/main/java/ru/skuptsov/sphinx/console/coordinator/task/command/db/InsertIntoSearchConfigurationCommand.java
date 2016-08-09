package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationFieldsService;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 23:44
 * To change this template use File | Settings | File Templates.
 */
@Component("INSERT_INTO_CONFIGURATION_SEARCH")
@Scope("prototype")
public class InsertIntoSearchConfigurationCommand extends DbCommand<ProcessTask> {
    @Autowired
	private ConfigurationService configurationService;
    @Autowired
    private ConfigurationFieldsService configurationFieldsService;

    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        logger.debug("INSERT_INTO_CONFIGURATION_" + task.getSphinxProcessType().name() + " EXECUTION...");
        
        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());

        Configuration configuration = configurationService.getConfiguration(task.getSearchConfigurationName());
        logger.info("CONFIGURATION: " + configuration);
        if(configuration == null) {
            configuration = task.getSearchConfiguration();
            logger.info("CONFIGURATION: " + configuration.getName());
        }
        
        if (configuration.getSourceConfigurationFields() != null) {
        	for (ConfigurationFields configurationFields : configuration.getSourceConfigurationFields()) {
        		configurationFields.setConfiguration(configuration);
        	}
        }

        if (task.getMainSqlQuery() != null) {
            ConfigurationFields configurationFields = new ConfigurationFields();
            configurationFields.setConfigurationType(ConfigurationType.SOURCE);
            configurationFields.setFieldKey("sql_query");
            configurationFields.setFieldValue(task.getMainSqlQuery());
            configurationFields.setIndexType(IndexType.MAIN);
            configurationFields.setConfiguration(configuration);
            configuration.getSourceConfigurationFields().add(configurationFields);
        }

        if(task.getCollection() != null && task.getCollection().getDelta() != null && task.getCollection().getDelta().getExternalAction() != null && task.getCollection().getDelta().getExternalAction().getDataSource() != null &&
                task.getCollection().getDelta().getExternalAction().getDataSource().getId() != null) {
            configuration.setDatasource(task.getCollection().getDelta().getExternalAction().getDataSource());
        }
        
        configuration = configurationService.save(configuration);
        
        task.setSearchConfiguration(configurationService.findById(configuration.getId(), "fieldMappings", "sourceConfigurationFields", "configurationTemplate", "configurationTemplate.configurationFields",
        		"searchConfigurationTemplate", "searchConfigurationTemplate.configurationFields",
        		"indexerConfigurationTemplate", "indexerConfigurationTemplate.configurationFields"));

        // save listen port
        Integer port = task.getSearchConfigurationPort();
        ConfigurationFields configurationField = new ConfigurationFields();
        configurationField.setFieldKey("listen");
        configurationField.setFieldValue(port.toString());
        configurationField.setConfigurationType(ConfigurationType.SEARCH);
        configurationField.setConfiguration(configuration);
        configurationFieldsService.save(configurationField);
        
        // save distributed port
        Integer distributedPort = task.getDistributedConfigurationPort();
        ConfigurationFields distributedConfigurationField = new ConfigurationFields();
        distributedConfigurationField.setFieldKey("distributed_listen");
        distributedConfigurationField.setFieldValue(distributedPort.toString());
        distributedConfigurationField.setConfigurationType(ConfigurationType.SEARCH);
        distributedConfigurationField.setConfiguration(configuration);
        configurationFieldsService.save(distributedConfigurationField);


        return status;
    }
}
