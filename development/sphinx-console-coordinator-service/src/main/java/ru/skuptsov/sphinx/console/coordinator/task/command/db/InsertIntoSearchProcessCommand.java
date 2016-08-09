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

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 23:38
 * To change this template use File | Settings | File Templates.
 */
@Component("INSERT_INTO_SPHINX_PROCESS_SEARCH")
@Scope("prototype")
public class InsertIntoSearchProcessCommand extends DbCommand<ProcessTask> {
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
        sphinxProcess.setCollection(collectionService.getCollection(task.getCollectionName()));
        logger.info("SEARCH SERVER NAME: " + task.getSearchServerName());
        sphinxProcess.setServer(serverService.getServer(task.getSearchServerName()));
        sphinxProcess.setConfiguration(configurationService.getConfiguration(task.getSearchConfigurationName()));
        sphinxProcess.setReplica(replicaService.findReplicaByNumber(task.getCollectionName(), task.getReplicaNumber()));
        sphinxProcess.setType(SphinxProcessType.SEARCHING);
        sphinxProcess.setConfigContent(generateSphinxConfService.generateContent(task, SphinxProcessType.SEARCHING).getBytes());
        
        Status status = Status.build(SystemInterface.COORDINATOR_DB, StatusCode.SUCCESS_CODE, task.getTaskUID());
        
        processService.save(sphinxProcess);

        return status;
    }
}
