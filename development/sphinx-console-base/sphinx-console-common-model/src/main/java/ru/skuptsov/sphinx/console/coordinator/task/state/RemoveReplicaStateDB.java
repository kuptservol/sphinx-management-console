package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum RemoveReplicaStateDB implements TaskState {
    REMOVE_REPLICA {
        @Override
        public void afterStateExecution(Task task) {

        }
    };
    
    @Override
    public String getStateName() {
        return name();
    }
}
