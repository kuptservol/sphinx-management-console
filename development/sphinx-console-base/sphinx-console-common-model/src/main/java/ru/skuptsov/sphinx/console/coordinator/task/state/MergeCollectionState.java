package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.IndexType;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;

public enum MergeCollectionState implements TaskState {
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

    STOP_INDEXING {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    
    START_MERGING {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    
    START_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
            task.setIndexType(IndexType.MAIN);

        }
    },
    
    
    START_PUSHING_FILES_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    
    SET_COORDINATOR_SEARCH {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    
    START_ROTATING_SEARCH {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    
    RUN_QUERY {
        @Override
		public void afterStateExecution(Task task) {

        }
    },
    
    END_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
            if(task instanceof ReplicaLoopTask && ((ReplicaLoopTask)task).hasNext()) {
                ((ReplicaLoopTask)task).next();
                task.setState(START_LOOP);
            } else {

            }
        }
    },
    
    MERGE_ACTION {
        @Override
		public void afterStateExecution(Task task) {

        }
    },
    
    ENABLE_SCHEDULED {
        @Override
		public void afterStateExecution(Task task) {

        }
    };
    
    @Override
    public String getStateName() {
        return name();
    }
}
