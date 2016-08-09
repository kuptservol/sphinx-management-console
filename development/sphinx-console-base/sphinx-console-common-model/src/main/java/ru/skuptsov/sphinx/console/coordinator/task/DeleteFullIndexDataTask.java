package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class DeleteFullIndexDataTask extends IndexNameTask implements FullIndexNameTask {
	public static final TaskName TASK_NAME = TaskName.DELETE_FULL_INDEX_DATA;
    private static final Chain CHAIN = Chain.DELETE_FULL_INDEX_DATA_CHAIN;

	private String collectionName;
	private String collectionFullRebuildName;

	@Override
	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
		this.collectionFullRebuildName = collectionName + FULL_REBUILD_SUFFIX;
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
