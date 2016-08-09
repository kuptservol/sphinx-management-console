Ext.define('sphinx-console.store.DataSources', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.Datasource',
    autoLoad: true,
    autoSync: true,
    proxy: {
        type: 'rest',
        format: 'json',
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/dataSources'
    }
});