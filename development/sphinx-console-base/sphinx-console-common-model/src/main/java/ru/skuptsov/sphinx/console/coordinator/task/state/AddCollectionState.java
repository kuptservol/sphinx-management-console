package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.IndexType;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum AddCollectionState implements TaskState {
	
    SET_COORDINATOR_SEARCH {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    },
    START_CREATING_SEARCHD_PROCESS {
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
    START_CREATING_INDEX_PROCESS {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    },
    START_UPDATING_SPHINX_CONFIG_SEARCH {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.INDEXING);
        }
    },
    START_UPDATING_SPHINX_CONFIG_INDEX {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.INDEXING);
        	task.setIndexType(IndexType.ALL);
            task.setStrictCopy(true);
        }
    },
    START_INDEXING_INDEX {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        	task.setIndexType(IndexType.ALL); // для наглядности, будет достаточно,что проставили выше
        	
        }
    },
//    START_INDEXING_SEARCH {
//        @Override
//        public void afterStateExecution(Task task) {
//            task.setIndexType(IndexType.ALL); // для наглядности, будет достаточно,что проставили выше
//
//        }
//    },
    START_PUSHING_FILES_INDEX {
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
    },
    ENABLE_SCHEDULED {
        @Override
        public void afterStateExecution(Task task) {

        }
    },
    COLLECTION_SET_PROCESS_SUCCESS {
        @Override
        public void afterStateExecution(Task task) {
            
        }
    };

    @Override
    public String getStateName() {
        return name();
    }
}
