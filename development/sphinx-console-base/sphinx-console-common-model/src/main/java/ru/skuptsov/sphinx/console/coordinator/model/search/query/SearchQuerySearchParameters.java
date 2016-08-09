package ru.skuptsov.sphinx.console.coordinator.model.search.query;

import ru.skuptsov.sphinx.console.coordinator.model.ReplicaName;
import ru.skuptsov.sphinx.console.coordinator.model.params.PageParameters;

import java.util.Date;

public class SearchQuerySearchParameters extends PageParameters {

    private String collectionName;
    private ReplicaName replicaName;
    private Date dateFrom;
    private Date dateTo;
    private Integer totalTimeMin;
    private Integer resultCountMin;
    private Boolean offsetNotZero;
    private Boolean resultCountZero;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Integer getTotalTimeMin() {
        return totalTimeMin;
    }

    public void setTotalTimeMin(Integer totalTimeMin) {
        this.totalTimeMin = totalTimeMin;
    }

    public Integer getResultCountMin() {
        return resultCountMin;
    }

    public void setResultCountMin(Integer resultCountMin) {
        this.resultCountMin = resultCountMin;
    }

    public Boolean getOffsetNotZero() {
        return offsetNotZero;
    }

    public void setOffsetNotZero(Boolean offsetNotZero) {
        this.offsetNotZero = offsetNotZero;
    }

    public Boolean getResultCountZero() {
        return resultCountZero;
    }

    public void setResultCountZero(Boolean resultCountZero) {
        this.resultCountZero = resultCountZero;
    }

    public ReplicaName getReplicaName() {
        return replicaName;
    }

    public void setReplicaName(ReplicaName replicaName) {
        this.replicaName = replicaName;
    }
}
