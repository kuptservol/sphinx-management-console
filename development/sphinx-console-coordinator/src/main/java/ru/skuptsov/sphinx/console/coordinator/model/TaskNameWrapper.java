package ru.skuptsov.sphinx.console.coordinator.model;

import ru.skuptsov.sphinx.console.coordinator.task.TaskName;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class TaskNameWrapper implements Serializable {

    private TaskName taskName;
    private String taskNameTitle;

    public TaskNameWrapper(TaskName taskName, String taskNameTitle) {
        this.taskName = taskName;
        this.taskNameTitle = taskNameTitle;
    }

    public TaskName getTaskName() {
        return taskName;
    }

    public void setTaskName(TaskName taskName) {
        this.taskName = taskName;
    }

    public String getTaskNameTitle() {
        return taskNameTitle;
    }

    public void setTaskNameTitle(String taskNameTitle) {
        this.taskNameTitle = taskNameTitle;
    }
}
