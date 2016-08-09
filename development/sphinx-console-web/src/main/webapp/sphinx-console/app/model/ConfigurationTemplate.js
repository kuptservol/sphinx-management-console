Ext.define('sphinx-console.model.ConfigurationTemplate', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'int', defaultValue: null, convert: null },
        {name: 'name', type: 'string', defaultValue: null, convert: null},
        {name: 'description', type: 'string', defaultValue: null, convert: null},
        {name: 'defaultTemplate', type: 'boolean', defaultValue: false, convert: null},
        {name: 'systemTemplate', type: 'boolean', defaultValue: false, convert: null},
        {name: 'type', type: 'string', defaultValue: null, convert: null}],
    hasMany:[
        {model: 'sphinx-console.model.ConfigurationFields', associationKey: 'configurationFields', associatedName: 'configurationFields', name: 'configurationFields'}
    ]
});
Ext.define('sphinx-console.model.ConfigurationTemplateIndexer', {
    extend: 'sphinx-console.model.ConfigurationTemplate'
});
Ext.define('sphinx-console.model.ConfigurationTemplateSearch', {
    extend: 'sphinx-console.model.ConfigurationTemplate'
});
Ext.define('sphinx-console.model.ConfigurationTemplateConfiguration', {
    extend: 'sphinx-console.model.ConfigurationTemplate'
});
