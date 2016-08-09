Ext.define('sphinx-console.model.Configuration', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'int'},
        {name: 'name', type: 'string'},
        {name: 'filePath', type: 'string'}],
    hasOne:[
        {model: 'sphinx-console.model.ConfigurationTemplate', getterName: 'getConfigurationTemplate', setterName: 'setConfigurationTemplate', associationKey: 'configurationTemplate', associationName: 'configurationTemplate', name: 'configurationTemplate', foreinhKey: 'id'},
        {model: 'sphinx-console.model.ConfigurationTemplateSearch', getterName: 'getSearchConfigurationTemplate', setterName: 'setSearchConfigurationTemplate', associationKey: 'searchConfigurationTemplate', associationName: 'searchConfigurationTemplate', name: 'searchConfigurationTemplate', instanceName: 'searchConfigurationTemplate', foreinhKey: 'id'},
        {model: 'sphinx-console.model.ConfigurationTemplateIndexer', getterName: 'getIndexerConfigurationTemplate', setterName: 'setIndexerConfigurationTemplate', associationKey: 'indexerConfigurationTemplate', associationName: 'indexerConfigurationTemplate', name: 'indexerConfigurationTemplate', instanceName: 'indexerConfigurationTemplate', foreinhKey: 'id'},
        {model: 'sphinx-console.model.Datasource', getterName: 'getDatasource', setterName: 'setDatasource', associationKey: 'datasource', associationName: 'datasource', name: 'datasource'}
    ],
    hasMany:[
        {model: 'sphinx-console.model.FieldMapping', name: 'fieldMappings', associationKey: 'fieldMappings'},
        {model: 'sphinx-console.model.ConfigurationFields', name: 'sourceConfigurationFields', associationKey: 'sourceConfigurationFields'}
    ],
    constructor: function(values) {
        if(values){
            this.superclass.constructor.call(this,values);
            this.setId(values.id == '' ? null : id);
        }else {
            this.superclass.constructor.call(this);
        }
    }
});
Ext.define('sphinx-console.model.SearchConfiguration', {
    extend: 'sphinx-console.model.Configuration'
});
Ext.define('sphinx-console.model.IndexConfiguration', {
    extend: 'sphinx-console.model.Configuration'
});
