Ext.define('sphinx-console.view.searchQuery.history.tabs.queryCountTab', {
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.queryCountTab',
    id: 'queryCountTabId',
    height: '100%',
    layout: 'fit',
    cls: 'dygraph-Div',
    methodName: 'searchQueryHistoryQueryCount',
    dygraphObject: null
});
