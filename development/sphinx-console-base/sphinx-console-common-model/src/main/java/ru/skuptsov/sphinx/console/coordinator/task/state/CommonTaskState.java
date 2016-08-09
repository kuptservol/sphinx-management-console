package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.DeleteCollectionTask;

public enum CommonTaskState implements TaskState {
     COMPLETED {
        @Override
        public void afterStateExecution(Task task) {
            task.setTaskStatus(TaskStatus.SUCCESS);
        }
     };

    @Override
    public String getStateName() {
        return name();
    }
}
