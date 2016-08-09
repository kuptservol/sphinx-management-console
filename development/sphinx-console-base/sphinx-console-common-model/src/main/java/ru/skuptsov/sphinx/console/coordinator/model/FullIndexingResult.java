package ru.skuptsov.sphinx.console.coordinator.model;

public class FullIndexingResult {
    private FullIndexingState fullIndexingState;
    private Server fullIndexingServer;
    private Long fullIndexingProcessId;
    private String indexingTaskUid;
    private String applyTaskUid;

    public FullIndexingState getFullIndexingState() {
        return fullIndexingState;
    }

    public void setFullIndexingState(FullIndexingState fullIndexingState) {
        this.fullIndexingState = fullIndexingState;
    }

    public Server getFullIndexingServer() {
        return fullIndexingServer;
    }

    public void setFullIndexingServer(Server fullIndexingServer) {
        this.fullIndexingServer = fullIndexingServer;
    }

    public Long getFullIndexingProcessId() {
        return fullIndexingProcessId;
    }

    public void setFullIndexingProcessId(Long fullIndexingProcessId) {
        this.fullIndexingProcessId = fullIndexingProcessId;
    }

    public String getApplyTaskUid() {
        return applyTaskUid;
    }

    public void setApplyTaskUid(String applyTaskUid) {
        this.applyTaskUid = applyTaskUid;
    }

    public String getIndexingTaskUid() {
        return indexingTaskUid;
    }

    public void setIndexingTaskUid(String indexingTaskUid) {
        this.indexingTaskUid = indexingTaskUid;
    }
}
