package ru.skuptsov.sphinx.console.coordinator.task.state;


import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum CreateReplicaStateDB implements TaskState {
	INSERT_INTO_REPLICA {
        @Override
		public void afterStateExecution(Task task) {
        	task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    INSERT_INTO_CONFIGURATION_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    INSERT_INTO_SPHINX_PROCESS_SEARCH {
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
