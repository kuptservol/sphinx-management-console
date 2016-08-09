Ext.define('sphinx-console.view.sphinxQLConsole.components.result.tabs.meta.MetaTab' , {
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.metaTab',
    requires: ['sphinx-console.view.sphinxQLConsole.components.result.tabs.meta.MetaGrid'],
    items: [
        {
            xtype:'panel',
            items:[
                {
                    xtype:'metaGrid',
                    columnWidth:.150
                }
            ]
        }
    ]
});