Ext.define('sphinx-console.store.ConfigurationTemplateConfiguration', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.ConfigurationTemplateConfiguration',
    requires: ['sphinx-console.store.SyncProxy'],
    proxy: {
        type: 'syncProxy',
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        api:{
           read:sphinx-console.util.Utilities.SERVER_URL + '/view/templates/configuration',
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
