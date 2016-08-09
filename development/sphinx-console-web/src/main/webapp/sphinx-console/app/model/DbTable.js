Ext.define('sphinx-console.model.DbTable', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    fields: [{name: 'name', type: 'string'}],
    hasMany: [
                {model: 'sphinx-console.model.DbTableColumn', getterName: 'getColumns', setterName: 'setColumns', associationKey: 'columns', associatedName: 'columns' }//, foreignKey: 'id'
             ]

});