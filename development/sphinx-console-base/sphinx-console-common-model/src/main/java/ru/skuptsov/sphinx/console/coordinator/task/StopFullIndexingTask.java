package ru.skuptsov.sphinx.console.coordinator.task;

public class StopFullIndexingTask extends StopIndexingTask implements FullIndexNameTask {
	public static final TaskName TASK_NAME = TaskName.STOP_FULL_INDEXING;

    private String collectionFullRebuildName;

    @Override
    public void setCollectionName(String collectionName) {
        super.setCollectionName(collectionName);
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

}
