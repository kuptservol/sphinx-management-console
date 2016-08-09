package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.annotation.ParallelSubflow;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum StartAllProcessesState implements TaskState {
	START_PARALLEL_SUBCHAIN {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    
    //
    @ParallelSubflow(name="START_ALL_PROCESSES_PARALLEL_CHAIN", start = true, end = false)
    SET_COORDINATOR_SEARCH {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    
    @ParallelSubflow(name="START_ALL_PROCESSES_PARALLEL_CHAIN", start = false, end = false)
    START_PROCESS {
        @Override
		public void afterStateExecution(Task task) {

        }
    },
    @ParallelSubflow(name="START_ALL_PROCESSES_PARALLEL_CHAIN", start = false, end = false)
    ADD_PROCESS_TO_STARTUP {
        @Override
        public void afterStateExecution(Task task) {

        }
    },
    @ParallelSubflow(name="START_ALL_PROCESSES_PARALLEL_CHAIN", start = false, end = true)
    ENABLE_SCHEDULED {
        @Override
		public void afterStateExecution(Task task) {

        }
    },
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
