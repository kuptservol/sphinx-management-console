package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.RemoveReplicaTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationFieldsService;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationService;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;
import ru.skuptsov.sphinx.console.spring.service.api.ReplicaService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 13.08.14
 * Time: 19:34
 * To change this template use File | Settings | File Templates.
 */
@Component("DELETE_FROM_REPLICA")
@Scope("prototype")
public class DeleteFromReplicaCommand extends DbCommand<RemoveReplicaTask> {
    @Autowired
    private ReplicaService replicaService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private ConfigurationFieldsService configurationFieldsService;
    @Autowired
    private CollectionService collectionService;


    @Override
    @SaveActivityLog
    public Status execute(RemoveReplicaTask task) {
        logger.info("DELETE_FROM_REPLICA STATE EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());

        String collectionName = task.getCollectionName();
        Replica replica = replicaService.findReplicaByNumber(collectionName, task.getReplicaNumber());
        logger.info("RETRIEVED REPLICA: " + replica + ", BY PARAMS: " + collectionName + ", " + task.getReplicaNumber());
        SphinxProcess sphinxProcess = processService.findByReplica(replica);
        
        Collection collection = sphinxProcess.getCollection();

        collectionService.setNeedReloadBySimpleCollection(collection);
        processService.clearDistributedCollectionAgents(sphinxProcess.getId());
        processService.delete(sphinxProcess);
        replicaService.delete(replica);
        if (sphinxProcess.getConfiguration() != null) {
            configurationService.delete(sphinxProcess.getConfiguration());
        }
        
        return status;
    }
}
