Ext.define('sphinx-console.store.DistributedCollectionName', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.CollectionName',
    autoLoad: false,
    autoSync: false,
    collectionName: null,
    proxy: {
        type: 'rest',
        format: 'json'
    },
    constructor: function(config) {
        this.callParent(arguments);
        this.proxy.url = sphinx-console.util.Utilities.SERVER_URL + '/view/distributedCollectionNames/' + this.collectionName;
    }
});