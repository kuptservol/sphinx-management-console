package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.task.DistributedReplicaLoopTask;

public enum ModifyDistributedCollectionAttributesStateDB implements TaskState {
	UPDATE_FULL_DISTRIBUTED_STRUCTURE {
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
    /*SAVE_COLLECTION {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
        }
    },*/
    UPDATE_DISTRIBUTED_CONFIGURATION_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    END_LOOP {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);
            if(task instanceof DistributedReplicaLoopTask && ((DistributedReplicaLoopTask)task).hasNext()) {
                ((DistributedReplicaLoopTask)task).next();
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
