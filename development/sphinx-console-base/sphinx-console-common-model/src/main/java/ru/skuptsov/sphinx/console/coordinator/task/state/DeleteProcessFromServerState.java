package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.DeleteProcessFromServerTask;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 14.08.14
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
public enum DeleteProcessFromServerState implements TaskState {
    STOP_PROCESS_IGNORE_FAILURE {
        @Override
        public void afterStateExecution(Task task) {
        }
    },
    REMOVE_PROCESS_FROM_STARTUP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    },
    START_DELETING_PROCESS {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },

    
	START_DELETING_INDEX_DATA_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    START_DELETING_INDEX_DATA_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.getFirst());

        }
    },

    START_LOOP {
        @Override
        public void afterStateExecution(Task task) {

        }
    },

    DELETE_FROM_SPHINX_PROCESS {
        @Override
        public void afterStateExecution(Task task) {

        }
    },

    DELETE_FROM_CONFIGURATION {
        @Override
        public void afterStateExecution(Task task) {

        }
    },

    END_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            SphinxProcessType nextType = SphinxProcessType.getNext(task.getSphinxProcessType());
            if(nextType != null) {
                task.setSphinxProcessType(SphinxProcessType.getNext(task.getSphinxProcessType()));
                task.setState(START_LOOP);
            } else {

            }
        }
    },

    DELETE_FROM_SERVER {
        @Override
        public void afterStateExecution(Task task) {

        }
    };

    @Override
    public String getStateName() {
        return name();
    }
}
