Ext.define('sphinx-console.view.sphinxQLConsole.components.result.tabs.result.ResultGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.resultGrid',
    columnLines: true,
    maxHeight: 380,
    viewConfig : {
        enableTextSelection: true
    },
    autoResizeColumns: true
});

