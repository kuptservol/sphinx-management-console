package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.ConfigurationFields;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.ModifyReplicaTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationFieldsService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;

@Component("UPDATE_REPLICA_CONFIGURATION")
@Scope("prototype")
public class UpdateReplicaConfigurationCommand extends DbCommand<ModifyReplicaTask> {

    @Autowired
    private ConfigurationFieldsService configurationFieldsService;

    @Autowired
    private ProcessService processService;
    
    @Autowired
    private CollectionService collectionService;


    @Override
    @SaveActivityLog
    public Status execute(ModifyReplicaTask task) {
        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        SphinxProcess searchSphinxProcess = processService.findSearchProcess(task.getCollectionName(), task.getReplicaNumber());
        Configuration configuration = searchSphinxProcess.getConfiguration();
        ConfigurationFields configurationField = configurationFieldsService.getSearchPort(configuration.getId());
        configurationField.setFieldValue(task.getNewSearchConfigurationPort().toString());
        configurationFieldsService.save(configurationField);
        
        if(task.getNewDistributedConfigurationPort() != null){
            ConfigurationFields distributedConfigurationField = configurationFieldsService.getDistributedPort(configuration.getId());

            distributedConfigurationField.setFieldValue(task.getNewDistributedConfigurationPort().toString());
            configurationFieldsService.save(distributedConfigurationField);
        }

        Collection collection = searchSphinxProcess.getCollection();

        collectionService.setNeedReloadBySimpleCollection(collection);

        return status;
    }
}
