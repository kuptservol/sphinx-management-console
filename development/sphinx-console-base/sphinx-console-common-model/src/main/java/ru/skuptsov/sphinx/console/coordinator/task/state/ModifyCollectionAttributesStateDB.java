package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;

/**
 * Created by Developer on 15.12.2014.
 */
public enum ModifyCollectionAttributesStateDB implements TaskState, TransactionalTaskState {
    SAVE_COLLECTION {
        @Override
        public void afterStateExecution(Task task) {

        }
    },
    //MODIFY_DB_CONFIGURATION
    UPDATE_CONFIGURATION_INDEX {
        @Override
        public void afterStateExecution(Task task) {

        }
    },
    START_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    UPDATE_CONFIGURATION_SEARCH {
        @Override
        public void afterStateExecution(Task task) {

        }
    },
    INSERT_INTO_SCHEDULED_TASK {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

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
    };

    @Override
    public String getStateName() {
        return name();
    }
}

