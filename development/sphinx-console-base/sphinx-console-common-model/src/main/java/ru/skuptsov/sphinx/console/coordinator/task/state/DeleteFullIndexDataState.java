package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum DeleteFullIndexDataState implements TaskState {

    SET_COORDINATOR_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);
        }
    },
    START_DELETING_INDEX_DATA_INDEX {
        @Override
        public void afterStateExecution(Task task) {
        }
    };

    @Override
    public String getStateName() {
        return name();
    }

}
