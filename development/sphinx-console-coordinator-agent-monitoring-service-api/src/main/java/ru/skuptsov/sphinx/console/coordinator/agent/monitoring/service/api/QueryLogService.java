package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuery;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lnovikova on 16.09.2015.
 */
public interface QueryLogService {
    Set<SearchQuery> getSearchQueriesResults(String collectionName, Long replicaNumber, Date lastParsedDate);
    Set<SearchQuery> ERROR_RESPONSE = null;
    HashSet<SearchQuery> EMPTY_RESULT = new HashSet<SearchQuery>();
}
