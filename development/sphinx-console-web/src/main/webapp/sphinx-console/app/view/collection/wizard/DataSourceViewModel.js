Ext.define('sphinx-console.view.collection.wizard.DataSourceViewModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.dataSourceSaveForm',

    data : {
        dataSource : null
    },
    view: 'dataSourcePanel',
    scheduler: {
        tickDelay: 2
    }
});