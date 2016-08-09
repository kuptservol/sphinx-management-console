package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.command.Command;
import ru.skuptsov.sphinx.console.coordinator.task.command.SyncCommand;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public abstract class DbCommand<T extends Task> extends CoordinatorCommand<T> {
    protected static final Logger logger = LoggerFactory.getLogger(DbCommand.class);

    @Override
    public Status.StatusCode getFailureCode() {
        return Status.StatusCode.FAILURE_LOCAL_DB_CODE;
    }

    @Override
    public Status.SystemInterface getSystemInterface() {
        return Status.SystemInterface.COORDINATOR_DB;
    }
}
