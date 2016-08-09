Ext.define('sphinx-console.model.Collection', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'int', useNull: true, defaultValue: null, allowNull: true},
        {name: 'name', type: 'string'},
        {name: 'description', type: 'string'},
        {name: 'collectionType', type: 'string'},
        {name: 'needReload', type: 'boolean', defaultValue: false, convert: null}
    ]
});
