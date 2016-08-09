package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.command.db.DbCommand;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 23:58
 * To change this template use File | Settings | File Templates.
 */
public abstract class Command<T extends Task> {
    protected static final Logger logger = LoggerFactory.getLogger(Command.class);

    public abstract Status execute(T task);
    public abstract Status.StatusCode getFailureCode();
    public abstract Status.SystemInterface getSystemInterface();

}
