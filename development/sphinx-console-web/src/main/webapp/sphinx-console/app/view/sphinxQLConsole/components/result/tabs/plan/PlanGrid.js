Ext.define('sphinx-console.view.sphinxQLConsole.components.result.tabs.plan.PlanGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.planGrid',
    forceFit: true,          //Fit to container
    columnLines: true,
    maxHeight: 363,
    viewConfig : {
        scroll:false,
        style:{overflow: 'auto',overflowX: 'hidden'},
        enableTextSelection: true
    },
    autoScroll: false,
    autoResizeColumns: true
});

