Ext.define('sphinx-console.view.sphinxQLConsole.components.result.ResultErrorPanel' ,{
    extend: 'Ext.form.Panel',
    alias : 'widget.resultErrorPanel',
    maxHeight: 500,
    requires: ['sphinx-console.view.sphinxQLConsole.components.result.ResultQueryLabel'],
    fieldDefaults: {
        labelAlign: 'left',
        msgTarget: 'side'
    },

    initComponent:function () {
        th = this;
        this.items = [
            {
                xtype:'panel',
                margin:"20 0 0 0",
                items:[
                    {
                        xtype:'panel',
                        layout:'vbox',
                        defaults:{
                            margin:"5 10 5 10"
                        },
                        items:[
                            {
                                xtype:'resultQueryLabel',
                                serverAndPort:th.serverAndPort,
                                queryText:th.queryText
                            },
                            {
                                xtype:'label',
                                text:'ERROR: ' + th.errorMessage
                            }
                        ]
                    }

                ]
            }
        ];
        this.callParent(arguments);
    }
});