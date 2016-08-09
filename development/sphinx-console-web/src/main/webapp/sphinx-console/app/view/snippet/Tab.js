Ext.define('sphinx-console.view.snippet.Tab', {
    itemId: 'snippetTabId',
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.snippetTab',
    requires: ['sphinx-console.view.snippet.List', 'sphinx-console.view.snippet.Search'],
    listeners: {
        activate: function () {
            Ext.getStore('SnippetData').load();
        }
    },
    items: [
        {
            region: 'north',
            xtype: 'snippetSearch'
        },
        {
            region: 'center',
            xtype: 'snippetList'
        }
    ]
});
