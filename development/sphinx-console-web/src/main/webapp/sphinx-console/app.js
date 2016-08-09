Ext.application({
    name: 'sphinx-console',
    autoCreateViewport: true,
    requires: ['sphinx-console.util.Utilities', 'sphinx-console.util.ErrorMessage', 'sphinx-console.view.PageSizePlugin','sphinx-console.util.InputTextMask','sphinx-console.util.CronExpressionValidator','sphinx-console.view.LoadPageSizePlugin', 'sphinx-console.view.collection.wizard.DataSourcePanel', 'sphinx-console.view.collection.wizard.DataSourceViewModel','sphinx-console.util.Date'],
    controllers: ['sphinx-console.controller.Collections','sphinx-console.controller.Replicas','sphinx-console.controller.Servers','sphinx-console.controller.Tasks'],
    models:['sphinx-console.model.Server','sphinx-console.model.Collection','sphinx-console.model.Status','sphinx-console.model.Task','sphinx-console.model.AdminProcess',
            'sphinx-console.model.ConfigurationTemplate','sphinx-console.model.ConfigurationFields','sphinx-console.model.CollectionWrapper','sphinx-console.model.Configuration',
            'sphinx-console.model.FieldMapping','sphinx-console.model.DbTable','sphinx-console.model.DbTableColumn','sphinx-console.model.Datasource','sphinx-console.model.CronScheduleWrapper',
            'sphinx-console.model.SearchConfigurationPort','sphinx-console.model.DistributedConfigurationPort','sphinx-console.view.logs.LogWindowDialog','sphinx-console.model.DistributedCollectionWrapper',
            'sphinx-console.model.SimpleCollectionWrapper','sphinx-console.model.SimpleCollectionReplicaWrapper'],
    stores:['TaskLog','ProcessType','ConfigurationTemplateIndexer','FieldMapping','ConfigurationTemplateSearch','ConfigurationTemplateConfiguration', 'DataSources',
            'ConfigurationTemplateDistributedConfiguration','ConfigurationTemplateDistributedSearch','SnippetData','TaskNames','SearchQueryData','CollectionName'],

    init : function() {
        Ext.util.Format.thousandSeparator = " ";
    },

    launch: function() {
    }
})