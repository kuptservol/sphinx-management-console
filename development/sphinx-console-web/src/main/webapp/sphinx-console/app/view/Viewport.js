Ext.define('sphinx-console.view.Viewport', {
    extend: 'Ext.container.Viewport',
    layout: 'border',
    style: {
        background: 'white'
    },
    requires: [
                'sphinx-console.view.collection.Tab',
                'sphinx-console.view.servers.Tab',
                'sphinx-console.view.tasks.Tab',
                'sphinx-console.view.snippet.Tab',
                'sphinx-console.view.searchQuery.Tab'
    ],
    defaults: {
        margin: 10,
        bodyPadding: '10%'
    },
    border: false,
    items :[
            {
                xtype: 'panel',
                html: '<h1>sphinx-console</h1> <span>PROJECT_VERSION</span>',
                region: 'north'
            }, {
                id: 'tabPanelId',
                itemId: 'tabPanelId',
                xtype: 'tabpanel',
                region: 'center',
                items: [
                    {
                        xtype: 'collectionTab',
                        layout: 'auto',
                        title: 'Коллекции'
                    },
                    {
                        xtype: 'serversTab',
                        layout: 'auto',
                        title: 'Сервера'
                    },
                    {
                        xtype: 'snippetTab',
                        layout: 'auto',
                        title: 'Сниппеты'
                    },
                    {
                        xtype: 'searchQueryTab',
                        layout: 'auto',
                        title: 'Анализ запросов'
                    },
                    {
                        xtype: 'tasksTab',
                        layout: 'auto',
                        title: 'Текущие задания'
                    }
                ]
            }]
});