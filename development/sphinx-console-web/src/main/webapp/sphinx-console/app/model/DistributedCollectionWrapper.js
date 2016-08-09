Ext.define('sphinx-console.model.DistributedCollectionWrapper', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    hasOne: [
                {model: 'sphinx-console.model.Collection', getterName: 'getCollection', setterName: 'setCollection', associationKey: 'collection', associatedName: 'collection' },
                {model: 'sphinx-console.model.SearchConfigurationPort', getterName: 'getSearchConfigurationPort', setterName: 'setSearchConfigurationPort', associationKey: 'searchConfigurationPort', associatedName: 'searchConfigurationPort' },
                {model: 'sphinx-console.model.Configuration', getterName: 'getSearchConfiguration', setterName: 'setSearchConfiguration', associationKey: 'searchConfiguration', associatedName: 'searchConfiguration', name: 'searchConfiguration', instanceName: 'searchConfiguration', foreingKey: 'id'},
                {model: 'sphinx-console.model.Server', getterName: 'getSearchServer', setterName: 'setSearchServer', associationKey: 'searchServer', associatedName: 'searchServer'}
            ],
    hasMany:[
        {model: 'sphinx-console.model.SimpleCollectionWrapper', name: 'nodes', associationKey: 'nodes'}
    ]        
});