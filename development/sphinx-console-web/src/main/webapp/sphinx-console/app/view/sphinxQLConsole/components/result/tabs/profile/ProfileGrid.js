Ext.define('sphinx-console.view.sphinxQLConsole.components.result.tabs.profile.ProfileGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.profileGrid',
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

