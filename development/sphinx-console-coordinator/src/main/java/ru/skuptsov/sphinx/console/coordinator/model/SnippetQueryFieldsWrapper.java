package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;
import java.util.List;

public class SnippetQueryFieldsWrapper implements Serializable {
    private String idName;
    
    private List<String> snippetConfigurationFields;

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public List<String> getSnippetConfigurationFields() {
		return snippetConfigurationFields;
	}

	public void setSnippetConfigurationFields(
			List<String> snippetConfigurationFields) {
		this.snippetConfigurationFields = snippetConfigurationFields;
	}
    
    
}
