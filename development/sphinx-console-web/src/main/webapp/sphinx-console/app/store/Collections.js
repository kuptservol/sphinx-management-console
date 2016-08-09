Ext.define('sphinx-console.store.Collections', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.Collection',
    requires: 'sphinx-console.model.Collection',
    autoLoad: true,
    autoSync: true,
    proxy: {
        type: 'rest',
        format: 'json',
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/collections',
        api: {
            create: sphinx-console.util.Utilities.SERVER_URL + '/configuration/data/collections/addCollection/',
            read:  sphinx-console.util.Utilities.SERVER_URL + '/view/data/collections',
//            update: '',
            destroy: sphinx-console.util.Utilities.SERVER_URL +  + '/view/data/collections/deleteCollection'
        }
    }
});
