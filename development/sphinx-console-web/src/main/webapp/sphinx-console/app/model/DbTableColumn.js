Ext.define('sphinx-console.model.DbTableColumn', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    fields: [{name: 'name', type: 'string'}, {name: 'type', type: 'string'}]
});