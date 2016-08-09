package ru.skuptsov.sphinx.console.coordinator.task.state;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.model.Task;
import ru.skuptsov.sphinx.console.coordinator.model.TaskStatus;
import ru.skuptsov.sphinx.console.coordinator.task.MoveProcessToServerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 18.08.14
 * Time: 0:31
 * To change this template use File | Settings | File Templates.
 */
public enum MoveProcessToServerState implements TaskState {

    SET_COORDINATOR_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    SET_COORDINATOR_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(null);

        }
    },
    DELETE_FROM_SCHEDULED_TASK {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    START_CREATING_SEARCHD_PROCESS {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    START_CREATING_INDEX_PROCESS {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    INSERT_INTO_SPHINX_PROCESS_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    INSERT_INTO_SPHINX_PROCESS_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    MOVE_FILES_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    MOVE_FILES_INDEX {
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
    START_UPDATING_SPHINX_CONFIG_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.SEARCHING);

        }
    },
    INSERT_INTO_CONFIGURATION_SEARCH {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(SphinxProcessType.INDEXING);

        }
    },
    INSERT_INTO_CONFIGURATION_INDEX {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(null);

        }
    },
    INSERT_INTO_SCHEDULED_TASK {
        @Override
        public void afterStateExecution(Task task) {
            task.setSphinxProcessType(null);

        }
    };

    @Override
    public String getStateName() {
        return name();
    }
}