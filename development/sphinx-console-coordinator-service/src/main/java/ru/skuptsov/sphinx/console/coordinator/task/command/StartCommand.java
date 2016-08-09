package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.command.db.CoordinatorCommand;

@Component("START")
@Scope("prototype")
public class StartCommand<T extends Task> extends CoordinatorCommand<T> implements SyncCommand {

    @Override
    @SaveActivityLog
    public Status execute(Task task) {

        return Status.build(Status.SystemInterface.COORDINATOR_CONFIGURATION, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());

    }

}
