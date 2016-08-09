package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyCollectionAttributesTask;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationFieldsService;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationService;

public class UpdateConfigurationCommand extends DbCommand<ModifyCollectionAttributesTask> {
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private ConfigurationFieldsService configurationFieldsService;

    @Override
    @SaveActivityLog
    public Status execute(ModifyCollectionAttributesTask task) {
        logger.debug("UPDATE_CONFIGURATION " + task.getSphinxProcessType() + " EXECUTION...");
        
        logger.info("TASK: " + task);

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        Configuration configuration;
        switch (task.getSphinxProcessType()) {
            case SEARCHING: {
                configuration = task.getSearchConfiguration();
                break;
            }
            case INDEXING: {
                configuration = task.getIndexConfiguration();
                break;
            }
            default: {
                configuration = null;
                break;
            }
        }

        if(configuration != null){
        	logger.info("CONFIGURATION, ID: " + configuration.getId());
        	logger.info("SOURCE CONFIGURATION FIELDS: " + configuration.getSourceConfigurationFields());
        	logger.info("FIRLD MAPPINGS: " + configuration.getFieldMappings());
        	
        	
        	for (ConfigurationFields field : configuration.getSourceConfigurationFields()) {
        		field.setId(null);
        	}
        	
        	for (FieldMapping field : configuration.getFieldMappings() ) {
        		field.setId(null);
        	}
        	
        	configurationService.clearFieldMappings(configuration.getId());
        	configurationService.clearSourceConfigurationFields(configuration.getId());
        	
        	if (task.getMainSqlQuery() != null && configuration.getMainSqlQueryField() == null) {
                ConfigurationFields configurationFields = new ConfigurationFields();
                configurationFields.setConfigurationType(ConfigurationType.SOURCE);
                configurationFields.setFieldKey("sql_query");
                configurationFields.setFieldValue(task.getMainSqlQuery());
                configurationFields.setIndexType(IndexType.MAIN);
                configurationFields.setConfiguration(configuration);
                configuration.getSourceConfigurationFields().add(configurationFields);
            }
        	
        	if (task.getDeltaSqlQuery() != null && configuration.getDeltaSqlQueryField() == null) {
                ConfigurationFields configurationFields = new ConfigurationFields();
                configurationFields.setConfigurationType(ConfigurationType.SOURCE);
                configurationFields.setFieldKey("sql_query");
                configurationFields.setFieldValue(task.getDeltaSqlQuery());
                configurationFields.setIndexType(IndexType.DELTA);
                configurationFields.setConfiguration(configuration);
                configuration.getSourceConfigurationFields().add(configurationFields);
            }
        	
        	// save configuration
            configurationService.save(configuration);
            // save listen port
            Integer port = task.getSearchConfigurationPort();
            ConfigurationFields configurationField = configurationFieldsService.getSearchPort(configuration.getId());
            if(configurationField != null){
                configurationField.setFieldValue(port.toString());
                configurationFieldsService.save(configurationField);
            }
            
            // save distributed port
            Integer distributedPort = task.getDistributedConfigurationPort();
            ConfigurationFields distributedConfigurationField = configurationFieldsService.getDistributedPort(configuration.getId());
            if(distributedConfigurationField != null){
            	distributedConfigurationField.setFieldValue(distributedPort.toString());
                configurationFieldsService.save(distributedConfigurationField);
            }
        }

        return status;
    }
}