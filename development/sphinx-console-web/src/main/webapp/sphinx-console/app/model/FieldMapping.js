Ext.define('sphinx-console.model.FieldMapping', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    fields: [
        {name: 'id', type: 'int'},
        {name: 'sourceField', type: 'string'},
        {name: 'sourceFieldType', type: 'string'},
        {name: 'indexField', type: 'string'},
        {name: 'indexFieldType', type: 'string'},
        {name: 'indexFieldCommentary', type:'string'},
        {name: 'isId', type: 'boolean'}
    ],
    statics: {
        indexFieldCleanup: function (name) {
            return name.replace(/[^a-zA-Z0-9_]+/g, '_');
        },
        indexFieldIsValid: function (name) {
            return name.toString().match(/^[a-zA-Z0-9_]+$/);
        }
    }
});
