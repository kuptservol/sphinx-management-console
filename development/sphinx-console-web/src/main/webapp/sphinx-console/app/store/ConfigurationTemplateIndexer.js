Ext.define('sphinx-console.store.ConfigurationTemplateIndexer', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.ConfigurationTemplateIndexer',
    requires: ['sphinx-console.store.SyncProxy'],
    proxy: {
        type: 'syncProxy',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        api:{
           read:sphinx-console.util.Utilities.SERVER_URL + '/view/templates/indexer',
           create:sphinx-console.util.Utilities.SERVER_URL + '/configuration/configurationTemplate/add',
           update:sphinx-console.util.Utilities.SERVER_URL + '/configuration/configurationTemplate/update',
           destroy:sphinx-console.util.Utilities.SERVER_URL + '/configuration/configurationTemplate/delete'
        },
        reader: {
            type: 'json'
        },
        writer:{
            type: 'json'
        }

    },
    autoLoad: true,
    autoSync: false,
    autoDestroy: false
});

