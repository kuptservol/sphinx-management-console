package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public enum DeleteCollectionStateDB implements TaskState, TransactionalTaskState {

    DELETE_COLLECTION {
        @Override
        public void afterStateExecution(Task task) {

        }
    }
    ;

    @Override
    public String getStateName() {
        return name();
    }
}
