package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;

/**
 * Created by Developer on 17.12.2014.
 */
public enum ModifyCollectionAttributesNoChangeStateDB implements TaskState {
    START_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    INSERT_INTO_SCHEDULED_TASK {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    SAVE_COLLECTION {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    },
    UPDATE_CONFIGURATION_SEARCH {
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
                task.setSphinxProcessType(SphinxProcessType.INDEXING);
                ((ReplicaLoopTask)task).first();
            }
        }
    },

    UPDATE_CONFIGURATION_INDEX {
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
