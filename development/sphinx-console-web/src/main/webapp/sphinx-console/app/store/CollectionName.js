Ext.define('sphinx-console.store.CollectionName', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.CollectionName',
    autoLoad: true,
    autoSync: false,
    proxy: {
        type: 'rest',
        format: 'json',
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/collectionNames'
    }
});