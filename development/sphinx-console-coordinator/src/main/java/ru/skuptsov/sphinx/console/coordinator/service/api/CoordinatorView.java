package ru.skuptsov.sphinx.console.coordinator.service.api;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.skuptsov.sphinx.console.coordinator.annotation.ExecuteOnlyWithValidParams;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorViewRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.CollectionSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.SearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.SnippetSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.params.TaskLogsSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryGrouped;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryHistoryPoint;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryHistorySearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuerySearchParameters;
import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLMultiResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public interface CoordinatorView {

    List<Server> getServers();

    List<Server> getServers(String sphinxProcessType);

	List<SphinxProcess> getProcesses();

	List<Collection> getCollections();

    Collection getCollection(String collectionName);

    ListDataViewWrapper<List<CollectionWrapper>> getCollectionViewData(CollectionSearchParameters collectionSearchParameters);

	List<ActivityLog> getActivityLog(ActivityLogSearchParameters parameter);

    List<DataSource> getDataSources();

	List<ScheduledTask> getScheduledTasks();

	List<ConfigurationTemplate> getSearchConfigurationTemplates();

    public ConfigurationTemplate getConfigurationTemplate(ConfigurationTemplate configurationTemplate);

	List<ConfigurationTemplate> getConfigurationTemplates();

	List<ConfigurationTemplate> getIndexerConfigurationTemplates();

    AdminProcess getAdminProcess(Long adminProcessId);

	Configuration getConfiguration(Long processId);

	TaskStatus getTaskStatus(String taskUid);

	ProcessStatus getProcessStatus(String processName);
	
	List<DBTable> getDBTables(DataSource dataSource);
	
	ServerStatus getServerStatus(Server server);

    Long getCollectionSize(String collectionName);
    
    ResponseWrapper<Boolean> isCurrentlyIndexing(String collectionName, SphinxProcessType type);

    ResponseWrapper<Boolean> isCurrentlyIndexingDelta(String collectionName);

    ResponseWrapper<Boolean> isCurrentlyMerging(String collectionName);

    List<AdminProcess> getAdminProcesses(Long serverId);
    
    ListDataViewWrapper<List<TaskDataViewWrapper>> getTasks(ActivityLogSearchParameters param);

    ListDataViewWrapper<List<Server>> getServers(SearchParameters param);
    
    ListDataViewWrapper<List<TaskDataViewWrapper>> getTaskLogs(TaskLogsSearchParameters param);

    ListDataViewWrapper<List<AdminProcess>> getAdminProcesses(SearchParameters param);
    
    byte[] getSphinxConf(String collectionName, Long replicaNumber);
    
    ServerStatus getAdminProcessStatus(AdminProcess adminProcess, HttpServletRequest request);
    
    ResponseWrapper<CollectionWrapper> getCollectionWrapper(String collectionName);

    byte[] getCollectionWrapperJson(String collectionName);

    byte[] getServerWrapperJson(String serverName);

    byte[] getSnippetWrapperJson(String collectionName);

    byte[] getSphinxConfPreview(CollectionWrapper collectionWrapper);
    
    byte[] getTaskErrorDescription(Long id);

    byte[] getTaskErrorDescription(String taskUid);

    List<String> getCollectionsUseTemplate(Long templateId);
    
    List<DBTableColumn> getQueryMetaData(SourceWrapper datasource);

    ResponseWrapper<Boolean> isMappingChanged(Configuration configuration);

    ConcurrentHashMap<String, CollectionInfoWrapper> getCollectionsInfo();

    ConcurrentHashMap<String, CollectionInfoWrapper> getCollectionsInfoFromAgent();
    
    ConcurrentHashMap<String, ServerInfoWrapper> getServersInfo();

    Set<Delta> getDeltas(String collectionName);

    List<Replica> getReplicas(String collectionName);

    ListDataViewWrapper<List<ReplicaWrapper>> getReplicasData(String collectionName);

    String getTaskComplete(String taskUid);
    
    ResponseWrapper<Boolean> isLastReplica(String collectionName);
    
    byte[] getSphinxLog(String collectionName, Long replicaNumber, Long recordNumber);

    ListDataViewWrapper<List<ReplicaWrapper>> getAvailableReplicas(String collectionName);

    SphinxQLMultiResult getSphinxQlMultyQueryResults(String serverName, Integer searchdPort, RequestWrapper<String> query);
    
    List<SimpleCollectionWrapper> getDistributedCollectionExtendedInformation(String collectionName);
    
    byte[] getDistributedSphinxConfPreview(DistributedCollectionWrapper collectionWrapper);
    
    List<SimpleCollectionWrapper> getAllSimpleCollections();
    
    List<ConfigurationTemplate> getDistributedConfigurationTemplates();
    
    ListDataViewWrapper<List<SimpleCollectionReplicaWrapper>> getSearchSphinxProcessesforCollection(String collectionName);
    
    List<ConfigurationTemplate> getSearchDistributedConfigurationTemplates();
    
    DistributedCollectionWrapper getDistributedCollectionWrapper(String collectionName);
    
    List<String> getCollectionsforSnippetCreation();
    
    List<String> getFieldsForSnippet(String collectionName);
    
    ListDataViewWrapper<List<SnippetConfigurationViewWrapper>> getSnippets(SnippetSearchParameters param);
    
    ConcurrentHashMap<String, SnippetInfoWrapper> getSnippetsInfo();
    
    SnippetConfigurationWrapper getSnippetConfigurationWrapper(String collectionName);
    
    SnippetQueryFieldsWrapper getSnippetQueryFields(RequestWrapper<String> sql);

    ResponseWrapper<String> getLastSnippetLogTaskUid(String collectionName);

    List<TaskNameWrapper> getTaskNames();

    ListDataViewWrapper<List<SearchQueryGrouped>> getSearchQueriesResultsGrouped(SearchQuerySearchParameters searchParameters);

    List<CollectionNameWrapper> getCollectionNames();

    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_TOTAL_TIME, method = RequestMethod.POST)
    @ResponseBody
    List<SearchQueryHistoryPoint> getQueryHistoryTotalTime(@RequestBody @Valid SearchQueryHistorySearchParameters searchParameters, BindingResult bindingResult);

    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_RESULT_COUNT, method = RequestMethod.POST)
    @ResponseBody
    List<SearchQueryHistoryPoint> getQueryHistoryResultCount(@RequestBody @Valid SearchQueryHistorySearchParameters searchParameters, BindingResult bindingResult);

    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_QUERY_COUNT, method = RequestMethod.POST)
    @ResponseBody
    List<SearchQueryHistoryPoint> getQueryHistoryQueryCount(@RequestBody @Valid SearchQueryHistorySearchParameters searchParameters, BindingResult bindingResult);

    @ExecuteOnlyWithValidParams
    @RequestMapping(value = CoordinatorViewRestURIConstants.SEARCH_QUERY_HISTORY_OFFSET_NOT_ZERO_COUNT, method = RequestMethod.POST)
    @ResponseBody
    List<SearchQueryHistoryPoint> getQueryHistoryOffsetNotZeroCount(@RequestBody @Valid SearchQueryHistorySearchParameters searchParameters, BindingResult bindingResult);

    @RequestMapping(value = CoordinatorViewRestURIConstants.DATE_DETAILINGS, method = RequestMethod.GET)
    @ResponseBody
    List<ValueTitle> getDateDetailings();

    @RequestMapping(value = CoordinatorViewRestURIConstants.DISTRIBUTED_COLLECTION_NAMES, method = RequestMethod.GET)
    @ResponseBody
    List<CollectionNameWrapper> getDistributedCollectionNames(@PathVariable String collectionName);
}
