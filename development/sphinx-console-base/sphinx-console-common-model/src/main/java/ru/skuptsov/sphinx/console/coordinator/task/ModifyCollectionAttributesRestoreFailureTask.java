package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class ModifyCollectionAttributesRestoreFailureTask extends ModifyCollectionAttributesTask {
    public static final TaskName TASK_NAME = TaskName.MODIFY_COLLECTION_ATTRIBUTES_RESTORE_FAILURE;
    private static final Chain CHAIN = Chain.MODIFY_COLLECTION_ATTRIBUTES_RESTORE_FAILURE_CHAIN;

    public ModifyCollectionAttributesRestoreFailureTask() {
        setSphinxProcessType(SphinxProcessType.SEARCHING);
    }

    @Override
    public TaskName getTaskName() {
        return TASK_NAME;
    }

    @Override
    public Chain getChain() {
        return CHAIN;
    }

}
