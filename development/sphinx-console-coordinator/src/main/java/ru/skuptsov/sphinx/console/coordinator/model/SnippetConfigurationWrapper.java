package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;

public class SnippetConfigurationWrapper implements Serializable {
    private String collectionName;
    
    private SnippetConfiguration snippetConfiguration;
    
    private CronScheduleWrapper cron;

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public SnippetConfiguration getSnippetConfiguration() {
		return snippetConfiguration;
	}

	public void setSnippetConfiguration(SnippetConfiguration snippetConfiguration) {
		this.snippetConfiguration = snippetConfiguration;
	}

	public CronScheduleWrapper getCron() {
		return cron;
	}

	public void setCron(CronScheduleWrapper cron) {
		this.cron = cron;
	}
    
    
}
