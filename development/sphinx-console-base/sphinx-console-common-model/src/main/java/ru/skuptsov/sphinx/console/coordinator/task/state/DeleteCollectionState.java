package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public enum DeleteCollectionState implements TaskState {
	SET_COORDINATOR_INDEX {
        @Override
        public void afterStateExecution(Task task) {
        }
	},
    DELETE_FROM_SCHEDULED_TASK {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);
        }
    },
    START_DELETING_INDEX_DATA_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);
        }
    },
    START_DELETING_SNIPPET_DATA_INDEX {
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
    START_LOOP {
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
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    },
	START_DELETING_INDEX_DATA_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
           task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    },
    START_DELETING_SNIPPET_DATA_SEARCH {
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
    END_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
            if(task instanceof ReplicaLoopTask && ((ReplicaLoopTask)task).hasNext()) {
                ((ReplicaLoopTask)task).next();
                task.setState(START_LOOP);
            }
        }
    };

    @Override
    public String getStateName() {
        return name();
    }
}
