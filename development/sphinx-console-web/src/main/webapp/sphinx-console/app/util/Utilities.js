Ext.define('sphinx-console.util.Utilities', {
    singleton: true,
    SERVER_URL: 'http://COORDINATOR_SERVER_NAME:COORDINATOR_SERVER_PORT/sphinx-console-coordinator/rest/coordinator',
    SERVER_TASK_INTERVAL: 30000,
    QUERY_LOG_TAB_ENABLED: query.log.enabled,
    DEBUG: DEBUG_CONFIG
});