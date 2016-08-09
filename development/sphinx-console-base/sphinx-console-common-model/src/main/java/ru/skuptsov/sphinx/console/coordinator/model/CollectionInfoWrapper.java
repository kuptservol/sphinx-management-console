package ru.skuptsov.sphinx.console.coordinator.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CollectionInfoWrapper {
    private Long collectionSize;
    private Boolean isCurrentlyIndexing;
    private Boolean isCurrentlyIndexingDelta;
    private Boolean isCurrentlyMerging;
    private FullIndexingResult fullIndexingResult;
    private Date lastIndexingTime;
    private Date nextIndexingTime;
    private Date lastMergeTime;
    private Date nextMergeTime;
    private Map<Long, ProcessStatus> processStatuses = new HashMap<Long, ProcessStatus>();
    private String allProcessStatus;
    private String taskUid;
    private boolean processingFailed = false;
    
	public Long getCollectionSize() {
		return collectionSize;
	}
	public void setCollectionSize(Long collectionSize) {
		this.collectionSize = collectionSize;
	}
	
	public Boolean getIsCurrentlyIndexing() {
		return isCurrentlyIndexing;
	}
	public void setIsCurrentlyIndexing(Boolean isCurrentlyIndexing) {
		this.isCurrentlyIndexing = isCurrentlyIndexing;
	}

    public Boolean getIsCurrentlyIndexingDelta() {
        return isCurrentlyIndexingDelta;
    }
    public void setIsCurrentlyIndexingDelta(Boolean isCurrentlyIndexingDelta) {
        this.isCurrentlyIndexingDelta = isCurrentlyIndexingDelta;
    }

    public Map<Long, ProcessStatus> getProcessStatuses() {
        return processStatuses;
    }

    public void setProcessStatuses(Map<Long, ProcessStatus> processStatuses) {
        this.processStatuses = processStatuses;
        setAllProcessStatus(processStatuses);
    }

    public Boolean getIsCurrentlyMerging() {
        return isCurrentlyMerging;
    }

    public void setIsCurrentlyMerging(Boolean isCurrentlyMerging) {
        this.isCurrentlyMerging = isCurrentlyMerging;
    }

    public FullIndexingResult getFullIndexingResult() {
        return fullIndexingResult;
    }

    public void setFullIndexingResult(FullIndexingResult fullIndexingResult) {
        this.fullIndexingResult = fullIndexingResult;
    }

    public String getTaskUid() {
        return taskUid;
    }

    public void setTaskUid(String taskUid) {
        this.taskUid = taskUid;
    }

    public String getAllProcessStatus() {
        return allProcessStatus;
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

    private void setAllProcessStatus(Map<Long, ProcessStatus> processStatuses) {
        boolean successProcessStatuses = false;
        boolean failureProcessStatuses = false;
        for(ProcessStatus processStatus : processStatuses.values()) {
            successProcessStatuses = successProcessStatuses || processStatus == ProcessStatus.SUCCESS;
            failureProcessStatuses = failureProcessStatuses || processStatus == ProcessStatus.FAILURE;
        }
        this.allProcessStatus = (successProcessStatuses && failureProcessStatuses) ? "NOT_ALL" : successProcessStatuses ? "ALL_SUCCESS" : "ALL_FAILURE";
    }
	public boolean getProcessingFailed() {
		return processingFailed;
	}
	public void setProcessingFailed(boolean processingFailed) {
		this.processingFailed = processingFailed;
	}
    
    
}
