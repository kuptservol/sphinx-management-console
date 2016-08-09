Ext.define('sphinx-console.view.searchQuery.history.tabs.offsetTab', {
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.offsetTab',
    id: 'offsetTabId',
    height: '100%',
    layout: 'fit',
    cls: 'dygraph-Div',
    methodName: 'searchQueryHistoryOffsetNotZeroCount',
    dygraphObject: null
});
