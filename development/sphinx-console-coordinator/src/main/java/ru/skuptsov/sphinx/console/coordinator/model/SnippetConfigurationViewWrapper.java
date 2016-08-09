package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;
import java.util.Date;

public class SnippetConfigurationViewWrapper implements Serializable {

    private String collectionName;

	private String cronSchedule;

    private Date lastBuildSnippet;
	
	private Date nextBuildSnippet;
    
    private SnippetInfoWrapper snippetInfoWrapper; 
    
    
	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public Date getLastBuildSnippet() {
		return lastBuildSnippet;
	}

	public void setLastBuildSnippet(Date lastBuildSnippet) {
		this.lastBuildSnippet = lastBuildSnippet;
	}

	public Date getNextBuildSnippet() {
		return nextBuildSnippet;
	}

	public void setNextBuildSnippet(Date nextBuildSnippet) {
		this.nextBuildSnippet = nextBuildSnippet;
	}

	public String getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}

	public SnippetInfoWrapper getSnippetInfoWrapper() {
		return snippetInfoWrapper;
	}

	public void setSnippetInfoWrapper(SnippetInfoWrapper snippetInfoWrapper) {
		this.snippetInfoWrapper = snippetInfoWrapper;
	}

	
    
    
}
