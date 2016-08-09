Ext.define('sphinx-console.view.searchQuery.history.QueryHistoryWindow', {
    extend : 'Ext.window.Window',
    width: '85%',
    height: '85%',
    layout: 'border',
    alias: 'widget.queryHistoryWindow',
    requires: [
        'sphinx-console.view.searchQuery.history.Search',
        'sphinx-console.view.searchQuery.history.Tabs'
    ],
//    maximizable: true,
    modal: true,
    title: 'История запроса',
    collectionName: null,
    serverAndPortValue: null,
    queryText: null,
    fromDate: null,
    fromTime: null,
    toDate: null,
    toTime: null,
    initComponent: function(){
        var th = this;
        this.items = [
                {
                    region: 'north',
                    xtype: 'queryHistorySearch',
                    collectionName: th.collectionName,
                    queryText: th.queryText,
                    serverAndPortValue: th.serverAndPortValue,
                    fromDate: th.fromDate,
                    fromTime: th.fromTime,
                    toDate: th.toDate,
                    toTime: th.toTime
                },
                {
                    region: 'center',
                    xtype: 'queryHistoryTabs'
                }
            ];
        this.callParent(arguments);
        Ext.getCmp('queryHistorySearchPanel').up('window').addListener('resize',this.resizeActiveGraph);
    },
    resizeActiveGraph: function(){
        var activeTab = Ext.getCmp('queryHistorySearchPanel').up('window').down('queryHistoryTabs').down('tabpanel').getActiveTab();
        if(activeTab && activeTab.dygraphObject){
            activeTab.dygraphObject.resize();
        }
    }

});