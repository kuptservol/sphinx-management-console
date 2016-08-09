Ext.define('sphinx-console.view.searchQuery.history.tabs.resultCountTab', {
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.resultCountTab',
    id: 'resultCountTabId',
    height: '100%',
    layout: 'fit',
    cls: 'dygraph-Div',
    methodName: 'searchQueryHistoryResultCount',
    dygraphObject: null
});
