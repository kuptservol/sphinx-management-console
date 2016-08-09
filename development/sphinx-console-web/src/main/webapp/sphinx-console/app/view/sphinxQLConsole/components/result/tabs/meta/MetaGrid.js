Ext.define('sphinx-console.view.sphinxQLConsole.components.result.tabs.meta.MetaGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.metaGrid',
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

