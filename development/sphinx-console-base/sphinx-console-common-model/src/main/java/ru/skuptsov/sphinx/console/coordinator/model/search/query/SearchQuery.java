package ru.skuptsov.sphinx.console.coordinator.model.search.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Replica;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchQuery extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4718349558157838178L;

	private Long id;
    private Collection collection;
    private String query;

    public SearchQuery() {
    }

    public SearchQuery(String query) {
        this.query = query;
    }

    @JsonIgnore
    private Set<SearchQueryResult> searchQueryResults;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Set<SearchQueryResult> getSearchQueryResults() {
        return searchQueryResults;
    }

    public void setSearchQueryResults(Set<SearchQueryResult> searchQueryResults) {
        this.searchQueryResults = searchQueryResults;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }
}
