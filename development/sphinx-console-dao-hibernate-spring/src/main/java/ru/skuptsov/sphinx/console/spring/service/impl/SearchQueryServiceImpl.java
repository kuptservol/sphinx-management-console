package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.*;
import ru.skuptsov.sphinx.console.dao.api.SearchQueryDao;
import ru.skuptsov.sphinx.console.spring.service.api.SearchQueryService;

import java.util.List;

@Service
public class SearchQueryServiceImpl extends AbstractSpringService<SearchQueryDao, SearchQuery> implements SearchQueryService {

    @Override
    @Transactional(readOnly = true)
    public SearchQuery getSearchQuery(Long collectionId, String query) {
        return getDao().getSearchQuery(collectionId, query);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryGrouped> getSearchQueries(SearchQuerySearchParameters searchParameters) {
        return getDao().getSearchQueries(searchParameters);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getForCount(SearchQuerySearchParameters searchParameters) {
        return getDao().getForCount(searchParameters);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryHistoryPoint> getQueryHistoryTotalTime(SearchQueryHistorySearchParameters searchParameters) {
        return getDao().getQueryHistoryTotalTime(searchParameters);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryHistoryPoint> getQueryHistoryResultCount(SearchQueryHistorySearchParameters searchParameters) {
        return getDao().getQueryHistoryResultCount(searchParameters);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryHistoryPoint> getQueryHistoryQueryCount(SearchQueryHistorySearchParameters searchParameters) {
        return getDao().getQueryHistoryQueryCount(searchParameters);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchQueryHistoryPoint> getQueryHistoryOffsetNotZeroCount(SearchQueryHistorySearchParameters searchParameters) {
        return getDao().getQueryHistoryOffsetNotZeroCount(searchParameters);
    }

    @Override
    @Transactional
    public void deleteAllSearchQueries() {
        getDao().deleteAllSearchQueries();
    }

    @Override
    @Transactional
    public void deleteOld(Integer queryLogKeepDay) {
        getDao().deleteOld(queryLogKeepDay);
    }
}
