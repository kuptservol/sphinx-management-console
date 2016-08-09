package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.IndexType;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

import ru.skuptsov.sphinx.console.coordinator.annotation.ParallelSubflow;

public enum RebuildCollectionState implements TaskState {
	

	SET_COORDINATOR_INDEX {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.INDEXING);
        	task.setIndexType(IndexType.DELTA);

        }
    },
    START_INDEXING_INDEX {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    START_PARALLEL_SUBCHAIN {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },

    //
    @ParallelSubflow(name="REBUILD_COLLECTION_PARALLEL_CHAIN", start = true, end = false)
    START_PUSHING_FILES_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    @ParallelSubflow(name="REBUILD_COLLECTION_PARALLEL_CHAIN", start = false, end = false)
    SET_COORDINATOR_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    @ParallelSubflow(name="REBUILD_COLLECTION_PARALLEL_CHAIN", start = false, end = true)
    START_ROTATING_SEARCH {
        @Override
        public void afterStateExecution(Task task) {

        }
    },

    END_PARALLEL_SUBCHAIN {
        @Override
        public void afterStateExecution(Task task) {

        }
    };
    //

    //

	@Override
    public String getStateName() {
        return name();
    }

}
