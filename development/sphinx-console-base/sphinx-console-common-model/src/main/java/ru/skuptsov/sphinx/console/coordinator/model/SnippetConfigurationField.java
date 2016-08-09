package ru.skuptsov.sphinx.console.coordinator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

public class SnippetConfigurationField extends BaseEntity {
	private Long id;
	
	@JsonIgnore
	private SnippetConfiguration snippetConfiguration;
	
	private String fieldName; 

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SnippetConfiguration getSnippetConfiguration() {
		return snippetConfiguration;
	}

	public void setSnippetConfiguration(SnippetConfiguration snippetConfiguration) {
		this.snippetConfiguration = snippetConfiguration;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	


	
}
