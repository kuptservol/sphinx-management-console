Ext.define('sphinx-console.store.ConfigurationFields', {
    extend: 'Ext.data.JsonStore',
    autoLoad: false,
    autoSync: false,
    sorters: 'id',
    sortOnLoad: true,
    model: 'sphinx-console.model.ConfigurationFields'
});

