Ext.define('sphinx-console.view.logs.Panel' ,{
    extend: 'Ext.form.Panel',
    alias : 'widget.logsPanel',
    requires: ['sphinx-console.view.logs.List'],
    bodyPadding: 1,
    layout: 'fit',    // for scroll enabling
    fieldDefaults: {
        labelAlign: 'left',
        msgTarget: 'side'
    },
    items: [{
        xtype: 'logsList',
        columnWidth: .150
    }]
});