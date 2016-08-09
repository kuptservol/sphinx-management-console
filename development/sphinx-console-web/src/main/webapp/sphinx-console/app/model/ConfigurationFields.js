Ext.define('sphinx-console.model.ConfigurationFields', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    fields: [
        {name: 'id', type: 'int', defaultValue: null, convert: null},
        {name: 'fieldKey', type: 'string', defaultValue: null, convert: null},
        {name: 'fieldValue', type: 'string', defaultValue: null, convert: null},
        {name: 'configurationType', type: 'string', defaultValue: null, convert: null},
        {name: 'fieldCommentary', type: 'string', defaultValue: null, convert: null}]
});
