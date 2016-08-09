Ext.define('sphinx-console.view.sphinxQLConsole.components.result.tabs.profile.ProfileTab' , {
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.profileTab',
    requires: ['sphinx-console.view.sphinxQLConsole.components.result.tabs.profile.ProfileGrid'],
    items: [
        {
            xtype:'panel',
            items:[
                {
                    xtype:'profileGrid',
                    columnWidth:.150
                }
            ]
        }
    ]
});