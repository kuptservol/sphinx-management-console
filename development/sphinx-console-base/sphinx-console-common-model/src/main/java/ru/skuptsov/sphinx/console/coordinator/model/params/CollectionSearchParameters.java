package ru.skuptsov.sphinx.console.coordinator.model.params;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andrey on 04.09.2014.
 */
public class CollectionSearchParameters extends PageParameters {

    List<Filter> filters = new LinkedList<Filter>();
    private Boolean pagingEnabled = true;

    public String getName() {
        Filter filter = findFilter("name", filters);
        return filter != null ? filter.getValue() : "";
    }

    public String getSearchServerName() {
        Filter filter = findFilter("'searchServerName'", filters);
        return filter != null ? filter.getValue() : "";
    }

    public String getIndexServerName() {
        Filter filter = findFilter("'indexServerName'", filters);
        return filter != null ? filter.getValue() : "";
    }

    public Boolean getPagingEnabled() {
        return pagingEnabled;
    }

    public void setPagingEnabled(Boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public void setName(String value){}

    public void setSearchServerName(String value){}

    public void setIndexServerName(String value){}

    public Filter findFilter(String name,List<Filter> filters) {
        for(Filter filter : filters) {
            if(filter.getProperty().equals(name)) {
                return filter;
            }
        }
        return null;
    }
}