package ru.skuptsov.sphinx.console.coordinator.search.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuerySearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.*;
import ru.skuptsov.sphinx.console.spring.service.api.SearchQueryResultService;
import ru.skuptsov.sphinx.console.spring.service.api.SearchQueryService;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by lnovikova on 16.09.2015.
 */
@Component
public class SearchQueryProcessService {

    @Autowired
    private SearchQueryService searchQueryService;

    @Autowired
    private SearchQueryResultService searchQueryResultService;

    public void addMissingData(Set<SearchQuery> searchQueries, Collection collection, Replica replica){
        for(SearchQuery searchQuery : searchQueries){
            searchQuery.setCollection(collection);
            for(SearchQueryResult searchQueryResult : searchQuery.getSearchQueryResults()){
                searchQueryResult.setReplica(replica);
            }
        }
    }

    public void addMissingData(List<SearchQueryGrouped> searchQueries, SearchQuerySearchParameters searchParameters){
        if(searchParameters.getReplicaName() != null){
            for(SearchQueryGrouped searchQuery : searchQueries){
                searchQuery.setReplicaName(searchParameters.getReplicaName().toString());
            }
        }
    }

    public void saveQueries(Set<SearchQuery> searchQueries){
        for(SearchQuery searchQuery : searchQueries){
            SearchQuery existingQuery = searchQueryService.getSearchQuery(searchQuery.getCollection().getId(), searchQuery.getQuery());
            if(existingQuery != null){
                searchQuery.setId(existingQuery.getId());
            }
            searchQueryService.save(searchQuery);
        }
    }

    public Date getLastParseDate(String collectionName, Long replicaNumber){
        return searchQueryResultService.getLastParseDate(collectionName, replicaNumber);
    }

    public List<SearchQueryGrouped> process(SearchQuerySearchParameters searchParameters){

        List<SearchQueryGrouped> result = searchQueryService.getSearchQueries(searchParameters);
        addMissingData(result, searchParameters);
        return result;

    }

    public Long getSearchQueryGroupedCount(SearchQuerySearchParameters searchParameters){
        return searchQueryService.getForCount(searchParameters);
    }

    public List<SearchQueryHistoryPoint> getQueryHistoryTotalTime(SearchQueryHistorySearchParameters searchParameters){

        List<SearchQueryHistoryPoint> results = searchQueryService.getQueryHistoryTotalTime(searchParameters);
        return results;

    }

    public List<SearchQueryHistoryPoint> getQueryHistoryResultCount(SearchQueryHistorySearchParameters searchParameters){

        List<SearchQueryHistoryPoint> results = searchQueryService.getQueryHistoryResultCount(searchParameters);
        return results;

    }

    public List<SearchQueryHistoryPoint> getQueryHistoryQueryCount(SearchQueryHistorySearchParameters searchParameters){

        List<SearchQueryHistoryPoint> results = searchQueryService.getQueryHistoryQueryCount(searchParameters);
        return results;

    }

    public List<SearchQueryHistoryPoint> getQueryHistoryOffsetNotZeroCount(SearchQueryHistorySearchParameters searchParameters){

        List<SearchQueryHistoryPoint> results = searchQueryService.getQueryHistoryOffsetNotZeroCount(searchParameters);
        return results;

    }

    public void deleteAllSearchQueries(){
        searchQueryService.deleteAllSearchQueries();
    }

    public void deleteOld(Integer queryLogKeepDay){
        searchQueryService.deleteOld(queryLogKeepDay);
    }
}
