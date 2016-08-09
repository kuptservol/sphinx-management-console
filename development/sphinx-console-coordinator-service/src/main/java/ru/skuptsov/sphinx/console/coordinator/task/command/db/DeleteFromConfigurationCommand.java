package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.ProcessTask;
import ru.skuptsov.sphinx.console.spring.service.api.ConfigurationService;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 13.08.14
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
@Component("DELETE_FROM_CONFIGURATION")
@Scope("prototype")
public class DeleteFromConfigurationCommand extends DbCommand<ProcessTask> {
    @Autowired
    private ConfigurationService configurationService;

    @Override
    @SaveActivityLog
    public Status execute(ProcessTask task) {
        logger.debug("DELETE_FROM_CONFIGURATION TYPE=" + task.getSphinxProcessType() + " EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
        String configurationName;
        switch (task.getSphinxProcessType()){
            case INDEXING:{
                configurationName = task.getIndexConfigurationName();
                break;
            }
            case SEARCHING:{
                configurationName = task.getSearchConfigurationName();
                break;
            }
            default:{
                configurationName = null;
                break;
            }
        }
        Configuration configuration = configurationName != null ? configurationService.getConfiguration(configurationName) : null;
        if(configuration != null) {
            logger.info("FOUND CONFIGURATION TO BE DELETED: " + configuration);
            configurationService.delete(configuration);
        }

        return status;
    }
}
