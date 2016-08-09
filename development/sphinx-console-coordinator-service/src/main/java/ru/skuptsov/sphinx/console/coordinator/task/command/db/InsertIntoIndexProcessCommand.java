package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateSphinxConfService;
import ru.skuptsov.sphinx.console.spring.service.api.*;

@Component("INSERT_INTO_SPHINX_PROCESS_INDEX")
@Scope("prototype")
public class InsertIntoIndexProcessCommand extends DbCommand<ProcessTask> {
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
    private GenerateSphinxConfService generateSphinxConfService;

    
    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        logger.debug("INSERT_INTO_SPHINX_PROCESS_" + task.getSphinxProcessType().name() + " EXECUTION...");

        SphinxProcess sphinxProcess = new SphinxProcess();
        sphinxProcess.setIndexName(task.getCollectionName());
        Collection collection = collectionService.getCollection(task.getCollectionName());
        sphinxProcess.setCollection(collection);
        sphinxProcess.setServer(serverService.getServer(task.getIndexServerName()));
        sphinxProcess.setConfiguration(configurationService.getConfiguration(task.getIndexConfigurationName()));
        sphinxProcess.setType(SphinxProcessType.INDEXING);
        sphinxProcess.setConfigContent(generateSphinxConfService.generateContent(task, SphinxProcessType.INDEXING).getBytes());
        sphinxProcess.setReplica(replicaService.findReplicaByNumber(task.getCollectionName(), task.getReplicaNumber()));

        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());
        processService.save(sphinxProcess);

        return status;
    }
}

