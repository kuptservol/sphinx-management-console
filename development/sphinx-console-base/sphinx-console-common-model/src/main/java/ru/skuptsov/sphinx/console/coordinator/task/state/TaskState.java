package ru.skuptsov.sphinx.console.coordinator.task.state;


import ru.skuptsov.sphinx.console.coordinator.model.Task;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
public interface TaskState extends Serializable {
    public static final TaskState COMPLETED = CommonTaskState.COMPLETED;
    public abstract void afterStateExecution(Task task);
    public abstract String getStateName();
}
