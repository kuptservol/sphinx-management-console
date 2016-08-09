package ru.skuptsov.sphinx.console.coordinator.controller;

public class CoordinatorConfigurationRestURIConstants {

	public static final String TEST = "/rest/coordinator/configuration/test";
	
	public static final String ADD_SERVER = "/rest/coordinator/configuration/server/add";
    public static final String DELETE_SERVER = "/rest/coordinator/configuration/server/delete/{id}";
    public static final String DELETE_SERVER_BY_NAME = "/rest/coordinator/configuration/server/delete/name/{name}";
    public static final String ADD_ADMIN_PROCESS = "/rest/coordinator/configuration/adminProcess/add";
    public static final String ADD_ADMIN_PROCESSES = "/rest/coordinator/configuration/adminProcesses/add";
    public static final String UPDATE_ADMIN_PROCESS = "/rest/coordinator/configuration/adminProcess/update";
    public static final String DELETE_ADMIN_PROCESS = "/rest/coordinator/configuration/adminProcess/delete/{id}";
	public static final String ADD_COLLECTION = "/rest/coordinator/configuration/addCollection";
    public static final String MAKE_COLLECTION_FULL_REBUILD_INDEX = "/rest/coordinator/configuration/makeCollectionFullRebuildIndex/{collectionName}/{serverName}";
    public static final String MAKE_COLLECTION_FULL_REBUILD_APPLY = "/rest/coordinator/configuration/makeCollectionFullRebuildApply/{collectionName}/{serverName}";
    public static final String DELETE_COLLECTION = "/rest/coordinator/configuration/deleteCollection/{collectionName}";
    public static final String REBUILD_COLLECTION = "/rest/coordinator/configuration/rebuildCollection/{collectionName}";
    public static final String MERGE_COLLECTION = "/rest/coordinator/configuration/mergeCollection/{collectionName}";
    public static final String MODIFY_COLLECTION_ATTRIBUTES = "/rest/coordinator/configuration/modifyCollectionAttributes";
    public static final String MOVE_PROCESS_TO_SERVER = "/rest/coordinator/configuration/moveProcessToServer";
    public static final String CHANGE_COLLECTION_UPDATE_SCHEDULE = "/rest/coordinator/configuration/changeCollectionUpdateSchedule";
    public static final String STOP_ALL_PROCESSES = "/rest/coordinator/configuration/stopProcess/{collectionName}";
    public static final String STOP_PROCESS = "/rest/coordinator/configuration/stopProcess/{collectionName}/replica/{replicaNumber}";
    public static final String START_ALL_PROCESSES = "/rest/coordinator/configuration/startProcess/{collectionName}";
    public static final String START_PROCESS = "/rest/coordinator/configuration/startProcess/{collectionName}/replica/{replicaNumber}";
    public static final String CHECK_DB_CONNECTION = "/rest/coordinator/configuration/checkDBConnection";
    public static final String STOP_TASK = "/rest/coordinator/configuration/stopTask";
    public static final String PAUSE_TASK = "/rest/coordinator/configuration/pauseTask";
    public static final String RESUME_TASK = "/rest/coordinator/configuration/resumeTask";
    public static final String STOP_INDEXING = "/rest/coordinator/configuration/stopIndexing/{collectionName}";
    public static final String STOP_FULL_REBUILD_INDEXING = "/rest/coordinator/configuration/stopFullRebuildIndexing/{collectionName}/{serverName}";
    public static final String STOP_MERGING = "/rest/coordinator/configuration/stopMerging/{collectionName}";
    public static final String VALIDATE_CRON_EXPRESSION = "/rest/coordinator/configuration/validateCronExpression";
    public static final String ADD_CONFIGURATION_TEMPLATE = "/rest/coordinator/configuration/configurationTemplate/add";
    public static final String UPDATE_CONFIGURATION_TEMPLATE = "/rest/coordinator/configuration/configurationTemplate/update/{id}";
    public static final String DELETE_CONFIGURATION_TEMPLATE = "/rest/coordinator/configuration/configurationTemplate/delete/{id}";
    public static final String ENABLE_SCHEDULING = "/rest/coordinator/configuration/enableScheduling/{collectionName}/{taskType}";
    public static final String DISABLE_SCHEDULING = "/rest/coordinator/configuration/disableScheduling/{collectionName}/{taskType}";
    public static final String CREATE_REPLICA = "/rest/coordinator/configuration/replica/create";
    public static final String REMOVE_REPLICA = "/rest/coordinator/configuration/replica/remove";
    public static final String MODIFY_REPLICA_PORT = "/rest/coordinator/configuration/replica/modifyPort";
    public static final String SAVE_DATA_SOURCE = "/rest/coordinator/configuration/dataSource/save";
    public static final String DELETE_FULL_INDEX_DATA = "/rest/coordinator/configuration/deleteFullIndexData/{collectionName}";
    public static final String ADD_DISTRIBUTED_COLLECTION = "/rest/coordinator/configuration/addDistributedCollection";
    public static final String RELOAD_DISTRIBUTED_COLLECTION = "/rest/coordinator/configuration/reloadDistributedCollection/{collectionName}";
    public static final String CREATE_DISTRIBUTED_REPLICA = "/rest/coordinator/configuration/replica/distributedCreate";
    public static final String MODIFY_DISTRIBUTED_REPLICA_PORT = "/rest/coordinator/configuration/replica/modifyDistributedReplicaPort";
    public static final String MODIFY_DISTRIBUTED_COLLECTION_ATTRIBUTES = "/rest/coordinator/configuration/modifyDistributedCollectionAttributes";
    public static final String CREATE_SNIPPET_CONFIGURATION = "/rest/coordinator/configuration/createSnippetConfiguration";
    public static final String DELETE_SNIPPET_CONFIGURATION = "/rest/coordinator/configuration/deleteSnippetConfiguration/{collectionName}";
    public static final String MAKE_SNIPPETS_FULL_REBUILD = "/rest/coordinator/configuration/makeSnippetsFullRebuild/{collectionName}";
    public static final String REBUILD_SNIPPETS = "/rest/coordinator/configuration/rebuildSnippets/{collectionName}";
    public static final String EDIT_SNIPPET_CONFIGURATION = "/rest/coordinator/configuration/editSnippetConfiguration";
    public static final String STOP_REBUILD_SNIPPETS = "/rest/coordinator/configuration/stopRebuildSnippets/{collectionName}";
    public static final String STOP_FULL_REBUILD_SNIPPETS = "/rest/coordinator/configuration/stopFullRebuildSnippets/{collectionName}";
    public static final String DELETE_ALL_SEARCH_QUERIES = "/rest/coordinator/configuration/deleteAllSearchQueries";
}
