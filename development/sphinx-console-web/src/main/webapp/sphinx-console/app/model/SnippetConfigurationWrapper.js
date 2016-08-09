Ext.define('sphinx-console.model.SnippetConfigurationWrapper', {
    extend: 'Ext.data.Model',
    idProperty: 'collectionName',
    fields: [
        {name: 'collectionName', type: 'string'}
    ],
    hasOne: [
        {model: 'sphinx-console.model.SnippetConfiguration', getterName: 'getSnippetConfiguration', setterName: 'setSnippetConfiguration', associationKey: 'snippetConfiguration', associatedName: 'snippetConfiguration' },
        {model: 'sphinx-console.model.CronScheduleWrapper', getterName: 'getCron', setterName: 'setCron', associationKey: 'cron', associatedName: 'cron' }
    ]
});
