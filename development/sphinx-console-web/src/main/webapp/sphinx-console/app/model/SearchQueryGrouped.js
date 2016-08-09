Ext.define('sphinx-console.model.SearchQueryGrouped', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id'},
        {name: 'query'},
        {name: 'collectionName'},
        {name: 'replicaName'},
        {name: 'replicaNameStr',
            calculate: function (data) {
                return data.replicaName ? data.replicaName : "-";
            }
        },
        {name: 'totalTimeMin'},
        {name: 'totalTimeMax'},
        {name: 'time',
            calculate: function (data) {
                return data.totalTimeMin == data.totalTimeMax ? data.totalTimeMin : data.totalTimeMin + " - " + data.totalTimeMax;
            }
        },
        {name: 'searchQueryResultCount'},
        {name: 'resultCountMin'},
        {name: 'resultCountMax'},
        {name: 'resultCount',
            calculate: function (data) {
                return data.resultCountMin == data.resultCountMax ? data.resultCountMin : data.resultCountMin + " - " + data.resultCountMax;
            }
        },
        {name: 'offsetNotZeroCount'}
    ]
});
