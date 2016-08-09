Ext.define('sphinx-console.store.AvailableReplicasData', {
    extend: 'sphinx-console.store.ReplicasData',
    autoLoad: true,

    constructor: function(config) {
        this.callParent(arguments);
        this.proxy.url = sphinx-console.util.Utilities.SERVER_URL + '/view/data/replicas/available/' + config.collectionName;
    }

});
