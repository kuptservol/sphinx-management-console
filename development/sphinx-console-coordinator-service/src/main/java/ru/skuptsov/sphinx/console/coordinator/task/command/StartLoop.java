package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.command.db.CoordinatorCommand;

@Component("START_LOOP")
@Scope("prototype")
public class StartLoop<T extends Task> extends CoordinatorCommand<T> implements SyncCommand {
    @Override
    public Status.SystemInterface getSystemInterface() {
        return Status.SystemInterface.COORDINATOR_CONFIGURATION;
    }

    @Override
    @SaveActivityLog
    public Status execute(T task) {
        String proccessTypeName = task.getSphinxProcessType() != null ? task.getSphinxProcessType().name() : "";
        logger.debug("START_LOOP " + proccessTypeName + " STATE EXECUTION...");

        Status status = Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());

        return status;
    }

}

