Ext.define('sphinx-console.view.servers.Tab' , {
    itemId: 'serversTabId',
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.serversTab',
    requires: ['sphinx-console.view.servers.List', 'sphinx-console.view.servers.Search','sphinx-console.view.servers.EditServerWindow','sphinx-console.view.servers.AddServerWindow'],
    items: [
        {
            region: 'north',
            xtype:  'serversSearch'
        },{
            region: 'center',
            xtype:  'serversList'
        }
    ]
});
