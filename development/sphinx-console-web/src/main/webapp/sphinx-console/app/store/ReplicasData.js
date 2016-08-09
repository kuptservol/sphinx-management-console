Ext.define('sphinx-console.store.ReplicasData', {
    extend: 'Ext.data.Store',
    requires: ['sphinx-console.util.Utilities', 'sphinx-console.store.sphinx-consoleProxy'],
    remoteSort: false,
    remoteFilter: true,
    sorters: ['name'],
    fields: [
            {
                name: 'replicaNumber'
            },{
                name: 'server'
            },{
                name: 'searchPort'
             },{
                name: 'searchConfigurationPort'
            },{
                name: 'distributedConfigurationPort'
            },{
                name: 'searchServerStatus',
                defaultValue: ''
            },{
                name: 'collectionType',
                defaultValue: ''
            },{
                name: 'serverAndPort',
                calculate: function (data) {
                    return data.server.name + " : " + data.searchPort;
                }
            }
    ],
    proxy: {
        type: 'sphinx-consoleProxy',
        format: 'json',
        paramsAsJson : true,
        reader: {
            type: 'json',
            rootProperty: 'list',
            totalProperty: 'total'
        }
    },

    constructor: function(config) {
        this.callParent(arguments);
        if(config.collectionName) {
            this.proxy.url = sphinx-console.util.Utilities.SERVER_URL + '/view/data/replicas/' + config.collectionName;
        }
    },

    setCollectionName: function(collectionName) {
        this.proxy.url = sphinx-console.util.Utilities.SERVER_URL + '/view/data/replicas/' + collectionName;
    }
});
