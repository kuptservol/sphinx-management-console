package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class ModifyDistributedCollectionAttributesRestoreFailureTask extends DistributedReplicaLoopTask {
	public static final TaskName TASK_NAME = TaskName.MODIFY_DISTRIBUTED_COLLECTION_ATTRIBUTES_RESTORE_FAILURE;
    private static final Chain CHAIN = Chain.MODIFY_DISTRIBUTED_COLLECTION_ATTRIBUTES_RESTORE_FAILURE_CHAIN;

    public ModifyDistributedCollectionAttributesRestoreFailureTask() {
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
