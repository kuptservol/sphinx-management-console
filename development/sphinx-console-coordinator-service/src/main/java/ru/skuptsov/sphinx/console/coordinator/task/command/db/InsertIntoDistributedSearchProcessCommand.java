package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateDistributedSphinxConfService;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ReplicaService;
import ru.skuptsov.sphinx.console.spring.service.api.ServerService;

@Component("INSERT_INTO_SPHINX_PROCESS_DISTRIBUTED_SEARCH")
@Scope("prototype")
public class InsertIntoDistributedSearchProcessCommand extends DbCommand<ProcessTask> {
    @Autowired
	private ServerService serverService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private ReplicaService replicaService;

    @Autowired
    private GenerateDistributedSphinxConfService generatedistributedSphinxConfService;


    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        logger.debug("INSERT_INTO_SPHINX_PROCESS_SEARCH_DISTRIBUTED EXECUTION...");
        
        SphinxProcess sphinxProcess = new SphinxProcess();
        sphinxProcess.setIndexName(task.getCollectionName());
        sphinxProcess.setCollection(collectionService.getCollection(task.getCollectionName()));
        logger.info("SEARCH SERVER NAME: " + task.getSearchServerName());
        sphinxProcess.setServer(serverService.getServer(task.getSearchServerName()));
        
        logger.info("SEARCH CONFIGURATION NAME: " + task.getSearchConfigurationName());
        
        Configuration configuration = configurationService.getConfiguration(task.getSearchConfigurationName());
        logger.info("CONFIGURATION: " + configuration);
        sphinxProcess.setConfiguration(configuration);
        sphinxProcess.setReplica(replicaService.findReplicaByNumber(task.getCollectionName(), task.getReplicaNumber()));
        sphinxProcess.setType(SphinxProcessType.SEARCHING);
        sphinxProcess.setConfigContent(generatedistributedSphinxConfService.generateContent(task).getBytes());
        
        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());
        
        processService.save(sphinxProcess);

        return status;
    }
}
