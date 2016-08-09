package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum AddDistributedCollectionState implements TaskState {
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
    
    START_UPDATING_SPHINX_CONFIG_DISTRIBUTED_SEARCH {
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
