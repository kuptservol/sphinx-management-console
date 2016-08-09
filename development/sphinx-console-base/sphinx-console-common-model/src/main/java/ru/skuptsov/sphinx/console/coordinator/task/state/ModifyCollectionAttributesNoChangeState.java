package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 26.08.14
 * Time: 4:57
 * To change this template use File | Settings | File Templates.
 */
public enum ModifyCollectionAttributesNoChangeState implements TaskState {
    DISABLE_SCHEDULED {
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
    START_UPDATING_SPHINX_CONFIG_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },

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
    START_UPDATING_SPHINX_CONFIG_SEARCH {
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
            if(task instanceof ReplicaLoopTask && ((ReplicaLoopTask)task).hasNext()) {
                ((ReplicaLoopTask)task).next();
                task.setState(START_LOOP);
            } else {

            }
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
