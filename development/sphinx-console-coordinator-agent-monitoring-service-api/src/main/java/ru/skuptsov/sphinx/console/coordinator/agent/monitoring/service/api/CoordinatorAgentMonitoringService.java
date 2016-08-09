package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.api;


import ru.skuptsov.sphinx.console.coordinator.model.ProcessStatus;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuery;
import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLMultiResult;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CoordinatorAgentMonitoringService {
	String test();
	
	ProcessStatus isProcessAlive(String processName);
    Long getCollectionSize(Integer port, Integer searchdPort, String collectionName);
    Boolean isCurrentlyIndexing(String collectionName);
    Boolean isCurrentlyIndexing(String processName, String indexName);
    Boolean isCurrentlyMerging(String processName);
    byte[] getRealSphinxConf(String collectionName) throws IOException;
    Boolean runQuery(Integer searchdPort, String collectionName);
    byte[] getSphinxLog(String collectionName, Long recordNumber) throws IOException;
    SphinxQLMultiResult getSphinxQLConsoleResult(Integer searchdPort, String query);
    Set<SearchQuery> getSearchQueriesResults(String collectionName, Long replicaNumber, Date lastParsedDate);

}
