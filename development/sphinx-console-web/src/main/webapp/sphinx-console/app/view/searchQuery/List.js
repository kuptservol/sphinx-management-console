Ext.define('sphinx-console.view.searchQuery.List', {
    extend: 'Ext.grid.Panel',
    id: 'searchQueryList',
    alias: 'widget.searchQueryList',
    requires: ['sphinx-console.util.Utilities','sphinx-console.view.searchQuery.history.QueryHistoryWindow'],
    store: 'SearchQueryData',
    forceFit: true,          //Fit to container
    columnLines: true,
    minHeight: 100,
    maxHeight: 550,
    viewConfig: {
        scroll: false,
        style: {overflow: 'auto', overflowX: 'hidden'}
    },
    autoScroll: false,
    snippetWizard: null,

    columns: [
        {
            xtype: 'actioncolumn',
            header: 'Действия',
            maxWidth: 100,
            minWidth: 100,
            align: 'center',
            items: [
                {
                    icon: 'app/resources/images/console.png',
                    tooltip: 'SphinxQL консоль',
                    handler: function (grid, rowIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        sphinx-console.app.fireEvent('openConsoleWindow', rec.get('collectionName'), rec.get('query'));
                    }
                },
                {
                    icon: 'app/resources/images/ico_chart.png',
                    tooltip: 'История запроса',
                    handler: function (grid, rowIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        var fromDate = Ext.getCmp('searchQuerySearchPanel').down('#fromDate').getValue();
                        var fromTime = Ext.getCmp('searchQuerySearchPanel').down('#fromTime').getValue();
                        var toDate = Ext.getCmp('searchQuerySearchPanel').down('#toDate').getValue();
                        var toTime = Ext.getCmp('searchQuerySearchPanel').down('#toTime').getValue();
                        Ext.create('sphinx-console.view.searchQuery.history.QueryHistoryWindow',
                            {collectionName:  rec.get('collectionName'),
                                queryText:  rec.get('query'),
                                serverAndPortValue: rec.get('replicaName'),
                                fromDate: fromDate,
                                fromTime: fromTime,
                                toDate: toDate,
                                toTime: toTime
                            }).show();
                    }
                }
            ]
        },
        {
            header: 'Запрос',
            dataIndex: 'query',
            cellWrap: true,
            flex: 1
        },
        {
            header: 'Коллекция',
            dataIndex: 'collectionName',
            maxWidth: 300,
            minWidth: 300
        },
        {
            header: 'Реплика',
            dataIndex: 'replicaNameStr',
            align: 'center',
            maxWidth: 220,
            minWidth: 220
        },
        {
            header: 'Время (мс)',
            dataIndex: 'time',
            align: 'center',
            maxWidth: 150,
            minWidth: 150
        },
        {
            header: 'Запросы',
            dataIndex: 'searchQueryResultCount',
            align: 'center',
            maxWidth: 150,
            minWidth: 150
        },
        {
            header: 'Результаты',
            dataIndex: 'resultCount',
            align: 'center',
            maxWidth: 150,
            minWidth: 150
        },
        {
            header: 'Переходы дальше 1-ой страницы',
            dataIndex: 'offsetNotZeroCount',
            align: 'center',
            cls: 'text-wrap',
            maxWidth: 150,
            minWidth: 150
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            items: [{
                xtype: 'button',
                text: 'Обновить',
                handler: function() {
                    Ext.getCmp('searchQuerySearchPanel').findSearchQueries();
                }
            }]
        },
        {
            xtype: 'pagingtoolbar',
            store: 'SearchQueryData',
            dock: 'bottom',
            plugins: [new sphinx-console.view.PageSizePlugin()],
            displayMsg: 'Записи {0} - {1} из {2}',
            beforePageText: 'Страница',
            afterPageText: 'из {0}',
            refreshText: 'Обновить',
            displayInfo: true
        }
    ]

});
