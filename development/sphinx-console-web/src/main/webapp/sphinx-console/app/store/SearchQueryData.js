Ext.define('sphinx-console.store.SearchQueryData', {
    extend: 'Ext.data.Store',
    requires: ['sphinx-console.util.Utilities', 'sphinx-console.store.sphinx-consoleProxy'],
    autoLoad: false,
    autoSync: false,
    pageSize: 10,
    model: 'sphinx-console.model.SearchQueryGrouped',
    proxy: {
        type: 'sphinx-consoleProxy',
        format: 'json',
        paramsAsJson : true,
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/searchQueriesResultsGrouped',
        actionMethods:  {create: "POST", read: "POST", update: "POST", destroy: "POST"},
        reader: {
            type: 'json',
            rootProperty: 'list',
            totalProperty: 'total'
        }
    }
});
