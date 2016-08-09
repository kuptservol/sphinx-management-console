package ru.skuptsov.sphinx.console.spring.service.api;

import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.*;

import java.util.List;

public interface SearchQueryService extends Service<SearchQuery> {
    @Transactional(readOnly = true)
    List<SearchQueryGrouped> getSearchQueries(SearchQuerySearchParameters searchParameters);

    @Transactional(readOnly = true)
    Long getForCount(SearchQuerySearchParameters searchParameters);

    @Transactional(readOnly = true)
    SearchQuery getSearchQuery(Long collectionId, String query);

    @Transactional(readOnly = true)
    List<SearchQueryHistoryPoint> getQueryHistoryTotalTime(SearchQueryHistorySearchParameters searchParameters);

    @Transactional(readOnly = true)
    List<SearchQueryHistoryPoint> getQueryHistoryResultCount(SearchQueryHistorySearchParameters searchParameters);

    @Transactional(readOnly = true)
    List<SearchQueryHistoryPoint> getQueryHistoryQueryCount(SearchQueryHistorySearchParameters searchParameters);

    @Transactional(readOnly = true)
    List<SearchQueryHistoryPoint> getQueryHistoryOffsetNotZeroCount(SearchQueryHistorySearchParameters searchParameters);

    @Transactional
    void deleteAllSearchQueries();

    @Transactional
    void deleteOld(Integer queryLogKeepDay);
}
