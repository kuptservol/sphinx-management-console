package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;

public enum MakeCollectionFullRebuildIndexState implements TaskState {

    SET_COORDINATOR_INDEX {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    START_CREATING_INDEX_PROCESS {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    START_UPDATING_SPHINX_CONFIG_INDEX {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    START_INDEXING_INDEX {
        @Override
		public void afterStateExecution(Task task) {

        }
    }
;

    @Override
    public String getStateName() {
        return name();
    }
}
