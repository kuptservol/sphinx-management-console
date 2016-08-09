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

@Component("INSERT_INTO_CONFIGURATION_INDEX")
@Scope("prototype")
public class InsertIntoIndexConfigurationCommand extends DbCommand<ProcessTask> {
    @Autowired
	private ConfigurationService configurationService;
    @Autowired
    private ConfigurationFieldsService configurationFieldsService;

    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        logger.debug("INSERT_INTO_CONFIGURATION_" + task.getSphinxProcessType().name() + " EXECUTION...");
        
        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());

        Configuration configuration = task.getIndexConfiguration();
        logger.info("CONFIGURATION: " + configuration);
        if(configuration == null) {
            configuration = configurationService.getConfiguration(task.getIndexConfigurationName());
        }
        logger.info("CONFIGURATION: " + configuration);
        
        if (configuration.getSourceConfigurationFields() != null) {
        	for (ConfigurationFields configurationFields : configuration.getSourceConfigurationFields()) {
        		configurationFields.setConfiguration(configuration);
        	}
        }

        if (task.getMainSqlQuery() != null) {
            ConfigurationFields sqlQueryField = configuration.getMainSqlQueryField() != null ? configuration.getMainSqlQueryField() : new ConfigurationFields();
            sqlQueryField.setConfigurationType(ConfigurationType.SOURCE);
            sqlQueryField.setFieldKey("sql_query");
            sqlQueryField.setFieldValue(task.getMainSqlQuery());
            sqlQueryField.setIndexType(IndexType.MAIN);
            sqlQueryField.setConfiguration(configuration);
            configuration.getSourceConfigurationFields().add(sqlQueryField);
        }

        if(task.getSearchConfiguration() != null && task.getSearchConfiguration().getDatasource() != null &&
            task.getSearchConfiguration().getDatasource().getId() != null) {
            configuration.setDatasource(task.getSearchConfiguration().getDatasource());
        }

        configuration = configurationService.save(configuration);

        task.setIndexConfiguration(configurationService.findById(configuration.getId(), "fieldMappings", "sourceConfigurationFields", "configurationTemplate", "configurationTemplate.configurationFields",
        		"searchConfigurationTemplate", "searchConfigurationTemplate.configurationFields",
        		"indexerConfigurationTemplate", "indexerConfigurationTemplate.configurationFields"));

        // save listen port
        Integer port = task.getSearchConfigurationPort();
        ConfigurationFields portField = configurationFieldsService.getSearchPort(configuration.getId());
        if(portField == null){
            portField = new ConfigurationFields();
            portField.setFieldKey("listen");
            portField.setConfigurationType(ConfigurationType.SEARCH);
            portField.setConfiguration(configuration);
        }
        portField.setFieldValue(port.toString());
        configurationFieldsService.save(portField);
        
        // save distributed port
        Integer distributedPort = task.getDistributedConfigurationPort();
        ConfigurationFields distributedPortField = configurationFieldsService.getDistributedPort(configuration.getId());
        if(distributedPortField == null){
        	distributedPortField = new ConfigurationFields();
        	distributedPortField.setFieldKey("distributed_listen");
        	distributedPortField.setConfigurationType(ConfigurationType.SEARCH);
        	distributedPortField.setConfiguration(configuration);
        }
        distributedPortField.setFieldValue(distributedPort.toString());
        configurationFieldsService.save(distributedPortField);


        return status;
    }
}
