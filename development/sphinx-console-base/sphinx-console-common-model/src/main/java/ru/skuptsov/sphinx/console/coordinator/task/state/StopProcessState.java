package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.StopProcessTask;


public enum StopProcessState implements TaskState {
	SET_COORDINATOR_SEARCH {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    STOP_PROCESS_IGNORE_FAILURE {
        @Override
		public void afterStateExecution(Task task) {

        }
    },
    REMOVE_PROCESS_FROM_STARTUP {
        @Override
		public void afterStateExecution(Task task) {

        }
    };/*,
    DISABLE_SCHEDULED {
        @Override
		public void afterStateExecution(Task task) {

        }
    };*/
    
    @Override
    public String getStateName() {
        return name();
    }
}
