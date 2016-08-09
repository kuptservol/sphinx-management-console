package ru.skuptsov.sphinx.console.coordinator.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Collection extends BaseEntity {
	private Long id;

    @NotEmpty
    private String name;
    @NotNull
    private CollectionType type;
    private String description;
    private Date lastIndexingTime;
    private Date nextIndexingTime;
    private Date lastMergeTime;
    private Date nextMergeTime;
    private boolean isProcessingFailed = true;
    @JsonIgnore
    @Valid
    private Set<ScheduledTask> scheduledTasks = new HashSet<ScheduledTask>();
    @JsonIgnore
    @Valid
    private Set<ActivityLog> activityLogs = new HashSet<ActivityLog>();
    @JsonIgnore
    @Valid
    private Set<SphinxProcess> sphinxProcesses = new HashSet<SphinxProcess>();
    
    @JsonIgnore
    private Set<DistributedCollectionNode> distributedCollectionNodes = new HashSet<DistributedCollectionNode>();
    
    /**
     * Для определения признака распределённой коллекции need_reload, смотрим, содержит ли какая-то распределённая коллекция, данную простую
     */
    @JsonIgnore
    private Set<DistributedCollectionNode> simpleCollectionInNodes = new HashSet<DistributedCollectionNode>();


    @Valid
    private Delta delta;
    
    @JsonIgnore
    @Valid
    private Set<Replica> replicas = new HashSet<Replica>();
    
    private CollectionRoleType collectionType = CollectionRoleType.SIMPLE;
    
    private Boolean needReload;
    
    @JsonIgnore
    private SnippetConfiguration snippetConfiguration;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CollectionType getType() {
        return type;
    }

    public void setType(CollectionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastIndexingTime() {
        return lastIndexingTime;
    }

    public void setLastIndexingTime(Date lastIndexingTime) {
        this.lastIndexingTime = lastIndexingTime;
    }

    public Date getNextIndexingTime() {
        return nextIndexingTime;
    }

    public void setNextIndexingTime(Date nextIndexingTime) {
        this.nextIndexingTime = nextIndexingTime;
    }

    public Date getLastMergeTime() {
        return lastMergeTime;
    }

    public void setLastMergeTime(Date lastMergeTime) {
        this.lastMergeTime = lastMergeTime;
    }

    public Date getNextMergeTime() {
        return nextMergeTime;
    }

    public void setNextMergeTime(Date nextMergeTime) {
        this.nextMergeTime = nextMergeTime;
    }

    public boolean getIsProcessingFailed() {
        return isProcessingFailed;
    }

    public void setIsProcessingFailed(boolean processingFailed) {
        isProcessingFailed = processingFailed;
    }

    public Set<ScheduledTask> getScheduledTasks() {
        return scheduledTasks;
    }

    public void setScheduledTasks(Set<ScheduledTask> scheduledTasks) {
        this.scheduledTasks = scheduledTasks;
    }

    public boolean getProcessingFailed() {
        return isProcessingFailed;
    }

    public void setProcessingFailed(boolean processingFailed) {
        isProcessingFailed = processingFailed;
    }

    public Set<SphinxProcess> getSphinxProcesses() {
        return sphinxProcesses;
    }

    public void setSphinxProcesses(Set<SphinxProcess> sphinxProcesses) {
        this.sphinxProcesses = sphinxProcesses;
    }

    public Set<ActivityLog> getActivityLogs() {
        return activityLogs;
    }

    public void setActivityLogs(Set<ActivityLog> activityLogs) {
        this.activityLogs = activityLogs;
    }

    public Delta getDelta() {
		return delta;
	}

	public void setDelta(Delta delta) {
		this.delta = delta;
	}

	public Set<Replica> getReplicas() {
        return replicas;
    }

    public void setReplicas(Set<Replica> replicas) {
        this.replicas = replicas;
    }

	public CollectionRoleType getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(CollectionRoleType collectionType) {
		this.collectionType = collectionType;
	}

	public Boolean getNeedReload() {
		return needReload;
	}

	public void setNeedReload(Boolean needReload) {
		this.needReload = needReload;
	}

	public Set<DistributedCollectionNode> getDistributedCollectionNodes() {
		return distributedCollectionNodes;
	}

	public void setDistributedCollectionNodes(
			Set<DistributedCollectionNode> distributedCollectionNodes) {
		this.distributedCollectionNodes = distributedCollectionNodes;
	}

	public Set<DistributedCollectionNode> getSimpleCollectionInNodes() {
		return simpleCollectionInNodes;
	}

	public void setSimpleCollectionInNodes(
			Set<DistributedCollectionNode> simpleCollectionInNodes) {
		this.simpleCollectionInNodes = simpleCollectionInNodes;
	}

	public SnippetConfiguration getSnippetConfiguration() {
		return snippetConfiguration;
	}

	public void setSnippetConfiguration(SnippetConfiguration snippetConfiguration) {
		this.snippetConfiguration = snippetConfiguration;
	}
    
    
}
