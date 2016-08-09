package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Status.StatusCode;
import ru.skuptsov.sphinx.console.coordinator.model.Status.SystemInterface;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.coordinator.template.GenerateSphinxConfService;
import ru.skuptsov.sphinx.console.spring.service.api.*;

@Component("INSERT_INTO_SPHINX_PROCESS")
@Scope("prototype")
public class InsertIntoProcessCommand extends DbCommand<ProcessTask> {
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
        SphinxProcessType sphinxProcessType = task.getSphinxProcessType();
        logger.debug("INSERT_INTO_SPHINX_PROCESS " + sphinxProcessType.name() + " EXECUTION...");

        SphinxProcess sphinxProcess = new SphinxProcess();
        sphinxProcess.setIndexName(task.getCollectionName());
        sphinxProcess.setCollection(collectionService.getCollection(task.getCollectionName()));
        sphinxProcess.setServer(task.getIndexServer());
        sphinxProcess.setConfiguration(task.getIndexConfiguration());
        sphinxProcess.setReplica(replicaService.findReplicaByNumber(task.getCollectionName(), task.getReplicaNumber()));
        sphinxProcess.setType(task.getSphinxProcessType());
        sphinxProcess.setConfigContent(generateSphinxConfService.generateContent(task, SphinxProcessType.INDEXING).getBytes());
        
        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE);
        
        processService.save(sphinxProcess);

        return status;
    }
}
