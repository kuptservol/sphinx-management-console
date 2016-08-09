package ru.skuptsov.sphinx.console.coordinator.task;

public interface FullIndexNameTask {
    String FULL_REBUILD_SUFFIX = "_full_rebuild";
    String getCollectionFullRebuildName();
}
