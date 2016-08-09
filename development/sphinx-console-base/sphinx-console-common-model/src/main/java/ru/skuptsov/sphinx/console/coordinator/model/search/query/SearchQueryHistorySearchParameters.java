package ru.skuptsov.sphinx.console.coordinator.model.search.query;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.ReplicaName;
import ru.skuptsov.sphinx.console.coordinator.model.common.DateDetailing;
import ru.skuptsov.sphinx.console.coordinator.model.params.PageParameters;

import java.util.Date;

public class SearchQueryHistorySearchParameters extends PageParameters {

    @NotEmpty
    private String collectionName;
    @NotEmpty
    private String query;
    private ReplicaName replicaName;
    private Date dateFrom;
    private Date dateTo;
    private DateDetailing dateDetailing;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ReplicaName getReplicaName() {
        return replicaName;
    }

    public void setReplicaName(ReplicaName replicaName) {
        this.replicaName = replicaName;
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

    public DateDetailing getDateDetailing() {
        return dateDetailing;
    }

    public void setDateDetailing(DateDetailing dateDetailing) {
        this.dateDetailing = dateDetailing;
    }
}
