Ext.define('sphinx-console.model.CollectionName', {
    extend: 'Ext.data.Model',
    idProperty: 'collectionName',
    autoLoad: false,
    autoSync: false,
    fields: [
        {name: 'collectionName', type: 'string'}
    ]
});
