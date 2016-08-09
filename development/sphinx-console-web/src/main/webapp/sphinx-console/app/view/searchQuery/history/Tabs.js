Ext.define('sphinx-console.view.searchQuery.history.Tabs', {
    extend: 'Ext.form.Panel',
    alias: 'widget.queryHistoryTabs',
    height: '100%',
    layout: 'fit',
    requires: [
        'sphinx-console.view.searchQuery.history.tabs.timeTab',
        'sphinx-console.view.searchQuery.history.tabs.queryCountTab',
        'sphinx-console.view.searchQuery.history.tabs.resultCountTab',
        'sphinx-console.view.searchQuery.history.tabs.offsetTab'
    ],
    items: [
        {
            itemId: 'tabPanelId',
            xtype: 'tabpanel',
            deferredRender: false,
            margin: 20,
            items: [
                {
                    xtype: 'timeTab',
                    layout: 'auto',
                    title: 'Время выполнения'
                },
                {
                    xtype: 'queryCountTab',
                    layout: 'auto',
                    title: 'Кол-во запросов'
                },
                {
                    xtype: 'resultCountTab',
                    layout: 'auto',
                    title: 'Кол-во результатов'
                },
                {
                    xtype: 'offsetTab',
                    layout: 'auto',
                    title: 'Переходы дальше 1-ой стр'
                }
            ],
            listeners: {
                tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                    if(newCard.dygraphObject){
                        newCard.dygraphObject.resize();
                    }
                }
            }
        }]

});