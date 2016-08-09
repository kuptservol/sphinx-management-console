Ext.define('sphinx-console.model.AdminProcess', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'int'},
        {name: 'port', type: 'int'},
        {name: 'type', type: 'string'}],
    associations: [{type: 'hasOne', model: 'sphinx-console.model.Server', associationKey: 'server'}]
});