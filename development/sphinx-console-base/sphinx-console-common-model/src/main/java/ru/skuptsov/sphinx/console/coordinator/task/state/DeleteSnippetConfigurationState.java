package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;

public enum DeleteSnippetConfigurationState implements TaskState {

    SET_COORDINATOR_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);
        }
    },
	START_DELETING_SNIPPET_DATA_INDEX {
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
	START_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
	},
	START_DELETING_SNIPPET_DATA_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    },
    END_LOOP {
        @Override
	    public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
	        if(task instanceof ReplicaLoopTask && ((ReplicaLoopTask)task).hasNext()) {
                ((ReplicaLoopTask)task).next();
	            task.setState(START_LOOP);
	        }
	    }
	};
	
    @Override
    public String getStateName() {
        return name();
    }
}
