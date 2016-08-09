package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.StartProcessTask;

public enum StartProcessState implements TaskState {
	SET_COORDINATOR_SEARCH {
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
    };
    
    @Override
    public String getStateName() {
        return name();
    }
}
