package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.IndexType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class MakeCollectionFullRebuildIndexTask extends ModifyCollectionAttributesTask implements MakeCollectionFullRebuildTask{
    public static final TaskName TASK_NAME = TaskName.MAKE_COLLECTION_FULL_REBUILD_INDEX;
    private static final Chain CHAIN = Chain.MAKE_COLLECTION_FULL_REBUILD_CHAIN;

    private String collectionFullRebuildName;


    public MakeCollectionFullRebuildIndexTask() {
        this.setIndexType(IndexType.ALL);
        this.setReplicaNumber(1l);
        this.setStrictCopy(true);
    }

    @Override
    public void setCollection(Collection collection) {
        super.setCollection(collection);
        this.collectionFullRebuildName = collection.getName() + FULL_REBUILD_SUFFIX;
    }

    public String getCollectionFullRebuildName() {
        return collectionFullRebuildName;
    }

    @Override
    public String getProcessName() {
        return getCollectionFullRebuildName();
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
