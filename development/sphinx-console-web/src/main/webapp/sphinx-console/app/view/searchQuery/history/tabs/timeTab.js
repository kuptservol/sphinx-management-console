Ext.define('sphinx-console.view.searchQuery.history.tabs.timeTab', {
    extend: 'Ext.form.Panel',
    alias: 'widget.timeTab',
    id: 'timePanelId',
    height: '100%',
    layout: 'fit',
    cls: 'dygraph-Div',
    methodName: 'searchQueryHistoryTotalTime',
    dygraphObject: null
});
