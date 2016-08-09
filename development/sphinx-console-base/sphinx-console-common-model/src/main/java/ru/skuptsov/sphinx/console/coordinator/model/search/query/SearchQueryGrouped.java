package ru.skuptsov.sphinx.console.coordinator.model.search.query;

public class SearchQueryGrouped {

    private Long searchQueryId;
    private String query;
    private String collectionName;
    private String replicaName;
    private Integer totalTimeMin;
    private Integer totalTimeMax;
    private Integer resultCountMin;
    private Integer resultCountMax;
    private Integer offsetNotZeroCount;
    private Integer searchQueryResultCount;

    public SearchQueryGrouped() {
    }

    public SearchQueryGrouped(Long searchQueryId, String query, String collectionName, Integer totalTimeMin, Integer totalTimeMax, Integer resultCountMin, Integer resultCountMax, Integer offsetNotZeroCount, Integer searchQueryResultCount) {
        this.searchQueryId = searchQueryId;
        this.query = query;
        this.collectionName = collectionName;
        this.totalTimeMin = totalTimeMin;
        this.totalTimeMax = totalTimeMax;
        this.resultCountMin = resultCountMin;
        this.resultCountMax = resultCountMax;
        this.offsetNotZeroCount = offsetNotZeroCount;
        this.searchQueryResultCount = searchQueryResultCount;
    }

    public Long getSearchQueryId() {
        return searchQueryId;
    }

    public void setSearchQueryId(Long searchQueryId) {
        this.searchQueryId = searchQueryId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getReplicaName() {
        return replicaName;
    }

    public void setReplicaName(String replicaName) {
        this.replicaName = replicaName;
    }

    public Integer getTotalTimeMin() {
        return totalTimeMin;
    }

    public void setTotalTimeMin(Integer totalTimeMin) {
        this.totalTimeMin = totalTimeMin;
    }

    public Integer getTotalTimeMax() {
        return totalTimeMax;
    }

    public void setTotalTimeMax(Integer totalTimeMax) {
        this.totalTimeMax = totalTimeMax;
    }

    public Integer getResultCountMin() {
        return resultCountMin;
    }

    public void setResultCountMin(Integer resultCountMin) {
        this.resultCountMin = resultCountMin;
    }

    public Integer getResultCountMax() {
        return resultCountMax;
    }

    public void setResultCountMax(Integer resultCountMax) {
        this.resultCountMax = resultCountMax;
    }

    public Integer getOffsetNotZeroCount() {
        return offsetNotZeroCount;
    }

    public void setOffsetNotZeroCount(Integer offsetNotZeroCount) {
        this.offsetNotZeroCount = offsetNotZeroCount;
    }

    public Integer getSearchQueryResultCount() {
        return searchQueryResultCount;
    }

    public void setSearchQueryResultCount(Integer searchQueryResultCount) {
        this.searchQueryResultCount = searchQueryResultCount;
    }
}
