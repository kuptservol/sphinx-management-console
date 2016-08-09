package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.StopIndexingTask;


public enum StopIndexingState implements TaskState {
	
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
    };
	
	
	@Override
    public String getStateName() {
        return name();
    }

	
}
