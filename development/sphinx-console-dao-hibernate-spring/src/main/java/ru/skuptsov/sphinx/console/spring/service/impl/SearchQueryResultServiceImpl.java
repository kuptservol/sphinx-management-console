package ru.skuptsov.sphinx.console.spring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryResult;
import ru.skuptsov.sphinx.console.dao.api.SearchQueryResultDao;
import ru.skuptsov.sphinx.console.spring.service.api.SearchQueryResultService;

import java.util.Date;

@Service
public class SearchQueryResultServiceImpl extends AbstractSpringService<SearchQueryResultDao, SearchQueryResult> implements SearchQueryResultService {

    @Override
    @Transactional(readOnly = true)
    public Date getLastParseDate(String collectionName, Long replicaNumber) {
        return getDao().getLastParseDate(collectionName, replicaNumber);
    }
}
