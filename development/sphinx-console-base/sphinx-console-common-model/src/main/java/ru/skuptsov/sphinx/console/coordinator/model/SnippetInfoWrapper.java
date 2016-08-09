package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;

public class SnippetInfoWrapper implements Serializable {

	private Boolean isCurrentlyRebuildSnippet = false;
	    
	private Boolean isCurrentlyFullRebuildSnippet = false;

	private Boolean isSheduleEnabled = false;

	public Boolean getIsCurrentlyRebuildSnippet() {
		return isCurrentlyRebuildSnippet;
	}

	public void setIsCurrentlyRebuildSnippet(Boolean isCurrentlyRebuildSnippet) {
		this.isCurrentlyRebuildSnippet = isCurrentlyRebuildSnippet;
	}

	public Boolean getIsCurrentlyFullRebuildSnippet() {
		return isCurrentlyFullRebuildSnippet;
	}

	public void setIsCurrentlyFullRebuildSnippet(
			Boolean isCurrentlyFullRebuildSnippet) {
		this.isCurrentlyFullRebuildSnippet = isCurrentlyFullRebuildSnippet;
	}

	public Boolean getIsSheduleEnabled() {
		return isSheduleEnabled;
	}

	public void setIsSheduleEnabled(Boolean isSheduleEnabled) {
		this.isSheduleEnabled = isSheduleEnabled;
	}
}
