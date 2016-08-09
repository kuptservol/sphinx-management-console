Ext.define('sphinx-console.model.Datasource', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    //clientIdProperty: 'id',
    identifier: {
        type: 'negative'
    },
    fields: [
        {name: 'id', type: 'int', allowNull: true},
        {name: 'name', type: 'string'},
        {name: 'type', type: 'string'},
        {name: 'host', type: 'string'},
        {name: 'port', type: 'int'},
        {name: 'user', type: 'string'},
        {name: 'password', type: 'string'},
        {name: 'odbcDsn', type: 'string'},
        {name: 'sqlDb', type: 'string'}
    ]
});