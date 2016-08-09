package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.spring.service.api.ProcessService;

import java.util.List;

@Component("DELETE_FROM_SPHINX_PROCESS")
@Scope("prototype")
public class DeleteFromProcessCommand extends DbCommand<Task> {
    @Autowired
    private ProcessService processService;

    @Override
    @SaveActivityLog
    public Status execute(Task task) {
        logger.debug("DELETE_FROM_SPHINX_PROCESS TYPE=" + task.getSphinxProcessType().name() + " EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());

        List<SphinxProcess> sphinxProcessprocessList = processService.findByCollectionNameAndType(task.getIndexServer().getId(), task.getCollectionName(), task.getSphinxProcessType());
        SphinxProcess sphinxProcess = CollectionUtils.isNotEmpty(sphinxProcessprocessList) ? sphinxProcessprocessList.get(0) : null; //TODO
        if(sphinxProcess != null) {
            logger.info("FOUND SPHINX PROCESS TO BE DELETED: " + sphinxProcess);
            processService.delete(sphinxProcess);
        }

        return status;
    }
}
