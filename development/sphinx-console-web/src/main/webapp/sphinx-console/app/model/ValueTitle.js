Ext.define('sphinx-console.model.ValueTitle', {
    extend: 'Ext.data.Model',
    idProperty: 'value',
    autoLoad: false,
    autoSync: false,
    fields: [
        {name: 'value', type: 'string'},
        {name: 'title', type: 'string'}
    ]
});
