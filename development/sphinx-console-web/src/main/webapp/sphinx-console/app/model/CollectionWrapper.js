Ext.define('sphinx-console.model.CollectionWrapper', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    hasOne: [
                {model: 'sphinx-console.model.Collection', getterName: 'getCollection', setterName: 'setCollection', associationKey: 'collection', associatedName: 'collection' },
                {model: 'sphinx-console.model.SearchConfigurationPort', getterName: 'getSearchConfigurationPort', setterName: 'setSearchConfigurationPort', associationKey: 'searchConfigurationPort', associatedName: 'searchConfigurationPort' },
                {model: 'sphinx-console.model.DistributedConfigurationPort', getterName: 'getDistributedConfigurationPort', setterName: 'setDistributedConfigurationPort', associationKey: 'distributedConfigurationPort', associatedName: 'distributedConfigurationPort' },
                {model: 'sphinx-console.model.Configuration', getterName: 'getSearchConfiguration', setterName: 'setSearchConfiguration', associationKey: 'searchConfiguration', associatedName: 'searchConfiguration', name: 'searchConfiguration', instanceName: 'searchConfiguration', foreingKey: 'id'},
                {model: 'sphinx-console.model.Configuration', getterName: 'getIndexConfiguration', setterName: 'setIndexConfiguration', associationKey: 'indexConfiguration', associatedName: 'indexConfiguration', name: 'indexConfiguration', instanceName: 'indexConfiguration', foreingKey: 'id'},
                {model: 'sphinx-console.model.CronScheduleWrapper', getterName: 'getCronSchedule', setterName: 'setCronSchedule', associationKey: 'cronSchedule', associatedName: 'cronSchedule' },
                {model: 'sphinx-console.model.CronScheduleWrapper', getterName: 'getMainCronSchedule', setterName: 'setMainCronSchedule', associationKey: 'mainCronSchedule', associatedName: 'mainCronSchedule' },
                {model: 'sphinx-console.model.Server', getterName: 'getSearchServer', setterName: 'setSearchServer', associationKey: 'searchServer', associatedName: 'searchServer'},
                {model: 'sphinx-console.model.Server', getterName: 'getIndexServer', setterName: 'setIndexServer', associationKey: 'indexServer', associatedName: 'indexServer'}
            ]
});