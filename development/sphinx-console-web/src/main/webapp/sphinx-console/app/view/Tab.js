Ext.define('sphinx-console.view.Tab' , {
    extend: 'Ext.form.Panel',
    layout: 'border',
    initComponent: function() {
        this.callParent(arguments);
        this.removeBodyCls('x-border-layout-ct');
    }
});