Ext.define('sphinx-console.view.sphinxQLConsole.components.result.tabs.result.ResultTab' , {
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.resultTab',
    requires: ['sphinx-console.view.sphinxQLConsole.components.result.tabs.result.ResultGrid'],
    items: [
        {
            xtype:'panel',
            items:[
                {
                    xtype:'resultGrid',
                    columnWidth:.150
                }
            ]
        }
    ]
});