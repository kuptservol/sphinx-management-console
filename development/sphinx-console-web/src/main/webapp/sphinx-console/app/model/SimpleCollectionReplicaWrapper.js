Ext.define('sphinx-console.model.SimpleCollectionReplicaWrapper', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'nodeHost', type: 'string'},
        {name: 'nodeDistribPort', type: 'string'}]
});
