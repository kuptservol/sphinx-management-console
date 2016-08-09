package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.IndexType;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum CreateReplicaState implements TaskState {
	SET_COORDINATOR_SEARCH {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    START_CREATING_SEARCHD_PROCESS {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    START_UPDATING_SPHINX_CONFIG_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
            task.setIndexType(IndexType.ALL);
            task.setPushIndexFilesForReplica(true);
            task.setStrictCopy(true);

        }
    },
    START_PUSHING_FILES_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    RSYNC_SNIPPET {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    },
    START_PROCESS {
        @Override
        public void afterStateExecution(Task task) {

        }
    },
    ADD_PROCESS_TO_STARTUP {
        @Override
        public void afterStateExecution(Task task) {

        }
    };

    @Override
    public String getStateName() {
        return name();
    }
}
