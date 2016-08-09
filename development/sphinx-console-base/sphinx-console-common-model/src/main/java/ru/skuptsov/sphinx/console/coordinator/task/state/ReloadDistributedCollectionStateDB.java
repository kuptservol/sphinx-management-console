package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

public enum ReloadDistributedCollectionStateDB implements TaskState, TransactionalTaskState {
	UPDATE_DISTRIBUTED_STRUCTURE {
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
