Ext.define('sphinx-console.store.CollectionData', {
    extend: 'Ext.data.Store',
    requires: ['sphinx-console.util.Utilities', 'sphinx-console.store.sphinx-consoleProxy'],
    remoteSort: false,
    remoteFilter: true,
    pageSize: 10,
    sorters: ['name'],
    fields: [{
                name: 'name',
                calculate: function (data) {
                    return data.collection.name;
                }
            },{
                name: 'collectionType',
                calculate: function (data) {
                    return data.collection.collectionType;
                }
            },{
                name: 'collection'
            },{
                name: 'collectionInfo'
            },{
                name: 'lastIndexingTime',
                calculate: function (data) {
                    return data.collection.lastIndexingTime ? new Date(data.collection.lastIndexingTime) : null;
                }
            },{
                name: 'nextIndexingTime',
                calculate: function (data) {
                    return data.collection.nextIndexingTime ? new Date(data.collection.nextIndexingTime) : null;
                }
            },{
                name: 'indexServer'
            },{
                name: 'indexServerPort'
            },{
                name: 'searchConfigurationPort'
            },{
                name: 'distributedConfigurationPort'
            },{
                name: 'collectionSize',
                calculate: function (data) {
                    return data.collectionInfo ? data.collectionInfo.collectionSize : '';
                }
            },{
                name: 'isIndexing',
                defaultValue: ''
            },{
                name: 'searchStatus',
                calculate: function (data) {
                    return data.collectionInfo ? data.collectionInfo.allProcessStatus == 'ALL_SUCCESS' : '';
                }
            },{
                name: 'description',
                calculate: function (data) {
                    return data.collection.description;
                }
            }],
    proxy: {
        type: 'sphinx-consoleProxy',
        format: 'json',
        filterParam: 'filters',
        paramsAsJson : true,
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/collections',
        actionMethods:  {create: "POST", read: "POST", update: "POST", destroy: "POST"},
        api: {
            create: sphinx-console.util.Utilities.SERVER_URL + '/configuration/data/collections/addCollection/',
            read:  sphinx-console.util.Utilities.SERVER_URL + '/view/data/collections',
//            update: '',
            destroy: sphinx-console.util.Utilities.SERVER_URL +  + '/view/data/collections/deleteCollection'
        },
        reader: {
            type: 'json',
            rootProperty: 'list',
            totalProperty: 'total'
        }
    }//,
//    filters: [Ext.create('Ext.util.Filter',{id: 'name', property:'name', value: ""}),
//        Ext.create('Ext.util.Filter',{id: 'searchServerName',property:'searchServerName', value: ""}),
//            Ext.create('Ext.util.Filter',{id: 'indexServerName',property:'indexServerName', value: ""})]
});
