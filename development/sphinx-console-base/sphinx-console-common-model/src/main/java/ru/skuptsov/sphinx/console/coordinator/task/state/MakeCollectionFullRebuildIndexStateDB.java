package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum MakeCollectionFullRebuildIndexStateDB implements TaskState, TransactionalTaskState {

    START {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    SAVE_COLLECTION {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    UPDATE_CONFIGURATION_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    UPDATE_CONFIGURATION_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.FULL_INDEXING);

        }
    },
    DELETE_FROM_SPHINX_PROCESS {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.FULL_INDEXING);

        }
    },
    INSERT_INTO_SPHINX_PROCESS {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    }
;

    @Override
    public String getStateName() {
        return name();
    }
}
