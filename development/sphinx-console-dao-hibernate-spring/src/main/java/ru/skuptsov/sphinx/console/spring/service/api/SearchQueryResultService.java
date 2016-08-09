package ru.skuptsov.sphinx.console.spring.service.api;

import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryResult;

import java.util.Date;

public interface SearchQueryResultService extends Service<SearchQueryResult> {
    Date getLastParseDate(String collectionName, Long replicaNumber);
}
