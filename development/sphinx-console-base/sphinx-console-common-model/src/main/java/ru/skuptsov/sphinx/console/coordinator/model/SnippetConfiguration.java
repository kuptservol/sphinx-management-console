package ru.skuptsov.sphinx.console.coordinator.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

public class SnippetConfiguration extends BaseEntity {
	private Long id;
	
	@JsonIgnore
	private Collection collection;
	
	@JsonIgnore
	private DataSource dataSource;
	
	@JsonIgnore
	private ScheduledTask scheduledTask;
	
	private String preQuery = "";
	
	private String postQuery = "";
	
	private String mainQuery = "";
	
    private String fullPreQuery = "";
	
	private String fullPostQuery = "";
	
	private String fullMainQuery = "";

	@JsonIgnore
	private Date lastBuildSnippet;

	@JsonIgnore
	private Date nextBuildSnippet;
	
	private Set<SnippetConfigurationField> fields = new HashSet<SnippetConfigurationField>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<SnippetConfigurationField> getFields() {
		return fields;
	}

	public void setFields(Set<SnippetConfigurationField> fields) {
		this.fields = fields;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ScheduledTask getScheduledTask() {
		return scheduledTask;
	}

	public void setScheduledTask(ScheduledTask scheduledTask) {
		this.scheduledTask = scheduledTask;
	}

	public String getPreQuery() {
		return preQuery;
	}

	public void setPreQuery(String preQuery) {
		this.preQuery = preQuery;
	}

	public String getPostQuery() {
		return postQuery;
	}

	public void setPostQuery(String postQuery) {
		this.postQuery = postQuery;
	}

	public String getMainQuery() {
		return mainQuery;
	}

	public void setMainQuery(String mainQuery) {
		this.mainQuery = mainQuery;
	}

	public String getFullPostQuery() {
		return fullPostQuery;
	}

	public void setFullPostQuery(String fullPostQuery) {
		this.fullPostQuery = fullPostQuery;
	}

	public String getFullMainQuery() {
		return fullMainQuery;
	}

	public void setFullMainQuery(String fullMainQuery) {
		this.fullMainQuery = fullMainQuery;
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

	public String getFullPreQuery() {
		return fullPreQuery;
	}

	public void setFullPreQuery(String fullPreQuery) {
		this.fullPreQuery = fullPreQuery;
	}
	
	
	

}
