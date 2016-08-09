package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 19.08.14
 * Time: 0:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class ProcessTask extends IndexNameTask {

    private Collection collection;
    private String cronSchedule;
    private String mergeDeltaCronSchedule;
    private boolean isDeltaSchedulingEnabledBeforeTaskExecution = true;
    private boolean isMergeSchedulingEnabledBeforeTaskExecution = true;
    private boolean isSnippetSchedulingEnabledBeforeTaskExecution = true;

    public String getSearchConfigurationName() {
        String configurationName = "";
        if(getSearchConfiguration() != null) {
            configurationName = getSearchConfiguration().getName();
        }

        return configurationName;
    }

    public String getIndexConfigurationName() {
        String configurationName = "";
        if(getIndexConfiguration() != null) {
            configurationName = getIndexConfiguration().getName();
        }

        return configurationName;
    }

    public void setCronSchedule(String cronSchedule) {
        this.cronSchedule = cronSchedule;
    }

    public void setMergeDeltaCronSchedule(String mergeDeltaCronSchedule) {
        this.mergeDeltaCronSchedule = mergeDeltaCronSchedule;
    }

    public String getCronSchedule() {
        return cronSchedule;
    }

    public String getMergeDeltaCronSchedule() {
        return mergeDeltaCronSchedule;
    }

    @Override
    public String getCollectionName() {
        return collection.getName();
    }

    public void setCollectionName(String collectionName) {
        collection.setName(collectionName);
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public boolean isDeltaSchedulingEnabledBeforeTaskExecution() {
        return isDeltaSchedulingEnabledBeforeTaskExecution;
    }

    public void setDeltaSchedulingEnabledBeforeTaskExecution(boolean isDeltaSchedulingEnabled) {
        this.isDeltaSchedulingEnabledBeforeTaskExecution = isDeltaSchedulingEnabled;
    }

    public boolean isMergeSchedulingEnabledBeforeTaskExecution() {
        return isMergeSchedulingEnabledBeforeTaskExecution;
    }

    public void setMergeSchedulingEnabledBeforeTaskExecution(boolean isMergeSchedulingEnabled) {
        this.isMergeSchedulingEnabledBeforeTaskExecution = isMergeSchedulingEnabled;
    }

    public boolean isSnippetSchedulingEnabledBeforeTaskExecution() {
        return isSnippetSchedulingEnabledBeforeTaskExecution;
    }

    public void setSnippetSchedulingEnabledBeforeTaskExecution(boolean isSnippetSchedulingEnabledBeforeTaskExecution) {
        this.isSnippetSchedulingEnabledBeforeTaskExecution = isSnippetSchedulingEnabledBeforeTaskExecution;
    }
}
