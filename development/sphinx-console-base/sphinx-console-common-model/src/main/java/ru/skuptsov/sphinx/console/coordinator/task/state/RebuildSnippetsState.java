package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;

public enum RebuildSnippetsState implements TaskState {
	
	DISABLE_SNIPPET_SCHEDULED {
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
    RUN_SNIPPET_QUERY {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);
        }
    },
    START_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);
        }
    },
    RSYNC_SNIPPET {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);
        }
    },
    END_LOOP {
	    @Override
	    public void afterStateExecution(Task task) {
	        task.setSphinxProcessType(SphinxProcessType.INDEXING);
	        if(task instanceof ReplicaLoopTask && ((ReplicaLoopTask)task).hasNext()) {
	            ((ReplicaLoopTask)task).next();
	            task.setState(START_LOOP);
	        }
	    }
    },
    ENABLE_SNIPPET_SCHEDULED {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    };
   
    @Override
    public String getStateName() {
        return name();
    }
}
