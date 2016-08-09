Ext.define('sphinx-console.store.TypedServersStore', {
    extend: 'Ext.data.Store',
    requires: ['sphinx-console.util.Utilities', 'sphinx-console.util.CollectionTaskRunner','sphinx-console.store.sphinx-consoleProxy'],
    remoteSort: true,
    remoteFilter: true,
    fields: [{name: 'name'}],
    autoLoad: true,

    constructor: function(config) {
        config = Ext.Object.merge({}, config);
        this.callParent([config]);
        this.proxy.url = sphinx-console.util.Utilities.SERVER_URL + '/view/servers/processType/' + config.processType;
    },

    proxy: {
        type: 'rest',
        format: 'json',
        filterParam: 'filters',
        paramsAsJson : true
    }
});
