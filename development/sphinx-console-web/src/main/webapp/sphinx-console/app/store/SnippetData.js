Ext.define('sphinx-console.store.SnippetData', {
    extend: 'Ext.data.Store',
    requires: ['sphinx-console.util.Utilities', 'sphinx-console.store.sphinx-consoleProxy'],
    remoteSort: false,
    remoteFilter: true,
    pageSize: 10,
    sorters: ['collectionName'],
    model: 'sphinx-console.model.SnippetConfigurationViewWrapper',
    proxy: {
        type: 'sphinx-consoleProxy',
        format: 'json',
        filterParam: 'filters',
        paramsAsJson : true,
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/snippets',
        actionMethods:  {create: "POST", read: "POST", update: "POST", destroy: "POST"},
        reader: {
            type: 'json',
            rootProperty: 'list',
            totalProperty: 'total'
        }
    }
});
