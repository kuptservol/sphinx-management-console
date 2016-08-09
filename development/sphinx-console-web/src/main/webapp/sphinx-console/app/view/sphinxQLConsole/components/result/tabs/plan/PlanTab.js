Ext.define('sphinx-console.view.sphinxQLConsole.components.result.tabs.plan.PlanTab' , {
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.planTab',
    requires: ['sphinx-console.view.sphinxQLConsole.components.result.tabs.plan.PlanGrid'],
    items: [
        {
            xtype:'panel',
            items:[
                {
                    xtype:'planGrid',
                    columnWidth:.150
                }
            ]
        }
    ]
});