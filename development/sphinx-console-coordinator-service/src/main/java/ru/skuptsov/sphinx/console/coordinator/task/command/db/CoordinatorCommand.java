package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.command.Command;
import ru.skuptsov.sphinx.console.coordinator.task.command.SyncCommand;

/**
 * Created with IntelliJ IDEA.
 * User: lnovikova
 * Date: 2/4/15
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CoordinatorCommand<T extends Task> extends Command<T> implements SyncCommand {

    protected static final Logger logger = LoggerFactory.getLogger(CoordinatorCommand.class);

    @Override
    public Status.StatusCode getFailureCode() {
        return Status.StatusCode.FAILURE_COORDINATOR_COMMAND;
    }

    @Override
    public Status.SystemInterface getSystemInterface() {
        return Status.SystemInterface.COORDINATOR_CONFIGURATION;
    }
}
