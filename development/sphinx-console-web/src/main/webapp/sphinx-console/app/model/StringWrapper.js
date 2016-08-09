Ext.define('sphinx-console.model.StringWrapper', {
    extend: 'Ext.data.Model',
    idProperty: 'result',
    autoLoad: false,
    autoSync: false,
    fields: [
        {name: 'result', type: 'string'}
    ]
});
