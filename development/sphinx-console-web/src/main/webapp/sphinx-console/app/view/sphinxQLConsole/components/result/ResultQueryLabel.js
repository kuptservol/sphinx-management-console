Ext.define('sphinx-console.view.sphinxQLConsole.components.result.ResultQueryLabel' ,{
    extend: 'Ext.form.Label',
    alias : 'widget.resultQueryLabel',
    width: '100%',

    initComponent: function(){
        this.text = th.serverAndPort + ' > ' + th.queryText;
        this.callParent(arguments);
    }
});