Ext.define('sphinx-console.model.SimpleCollectionWrapper', {
    extend: 'Ext.data.Model',
    idProperty: 'collectionName',
    fields: [
        {name: 'collectionName', type: 'string'}
        ],
    hasMany:[
        {model: 'sphinx-console.model.SimpleCollectionReplicaWrapper', name: 'agents', associationKey: 'agents'}
    ]
});
