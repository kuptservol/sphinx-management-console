package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.annotation.ParallelSubflow;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum StopAllProcessesState implements TaskState {
	START_PARALLEL_SUBCHAIN {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    
    //
    @ParallelSubflow(name="STOP_ALL_PROCESSES_PARALLEL_CHAIN", start = true, end = false)
    SET_COORDINATOR_SEARCH {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    
    @ParallelSubflow(name="STOP_ALL_PROCESSES_PARALLEL_CHAIN", start = false, end = false)
    STOP_PROCESS_IGNORE_FAILURE {
        @Override
		public void afterStateExecution(Task task) {

        }
    },
    @ParallelSubflow(name="STOP_ALL_PROCESSES_PARALLEL_CHAIN", start = false, end = true)
    REMOVE_PROCESS_FROM_STARTUP {
        @Override
        public void afterStateExecution(Task task) {

        }
    },

   /* @ParallelSubflow(name="STOP_ALL_PROCESSES_PARALLEL_CHAIN", start = false, end = true)
    DISABLE_SCHEDULED {
        @Override
		public void afterStateExecution(Task task) {

        }
    },*/
    
    END_PARALLEL_SUBCHAIN {
        @Override
        public void afterStateExecution(Task task) {

        }
    };
    
    @Override
    public String getStateName() {
        return name();
    }

}
