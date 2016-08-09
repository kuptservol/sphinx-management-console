package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.DistributedReplicaLoopTask;

public enum ReloadDistributedCollectionState implements TaskState {

    START_LOOP {
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
    STOP_PROCESS_IGNORE_FAILURE {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
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

        }
    },
    END_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
            if(task instanceof DistributedReplicaLoopTask && ((DistributedReplicaLoopTask)task).hasNext()) {
                ((DistributedReplicaLoopTask)task).next();
                task.setState(START_LOOP);
            }
        }
    };

    @Override
    public String getStateName() {
        return name();
    }
}
