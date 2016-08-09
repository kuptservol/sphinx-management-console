package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.annotation.ParallelSubflow;
import ru.skuptsov.sphinx.console.coordinator.model.IndexType;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;

public enum MakeCollectionFullRebuildApplyIndexState implements TaskState {

    START {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);
        }
    },
    DISABLE_SCHEDULED {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    SET_COORDINATOR_INDEX {
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
    START_DELETING_INDEX_DATA_FILES {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    START_PUSHING_FILES_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    };

	@Override
    public String getStateName() {
        return name();
    }

}
