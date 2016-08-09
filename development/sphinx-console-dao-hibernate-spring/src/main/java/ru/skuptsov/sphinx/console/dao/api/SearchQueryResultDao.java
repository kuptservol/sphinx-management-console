package ru.skuptsov.sphinx.console.dao.api;

import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuery;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryGrouped;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryResult;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuerySearchParameters;
import ru.skuptsov.sphinx.console.dao.common.api.Dao;

import java.util.Date;
import java.util.List;


public interface SearchQueryResultDao extends Dao<SearchQueryResult> {
	Date getLastParseDate(String collectionName, Long replicaNumber);
}
