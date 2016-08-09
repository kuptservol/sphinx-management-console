package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum ModifyDistributedReplicaPortState implements TaskState {
    SET_COORDINATOR_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    STOP_PROCESS_IGNORE_FAILURE {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
            task.setSearchConfigurationPort(task.getNewSearchConfigurationPort());
            

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
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    };
    
    @Override
    public String getStateName() {
        return name();
    }
}


