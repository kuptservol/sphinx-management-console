Ext.define('sphinx-console.view.sphinxQLConsole.components.result.ResultPanel' ,{
    extend: 'Ext.form.Panel',
    alias : 'widget.resultPanel',
    requires: ['sphinx-console.view.sphinxQLConsole.components.result.ResultQueryLabel',
                'sphinx-console.view.sphinxQLConsole.components.result.tabs.result.ResultTab',
                'sphinx-console.view.sphinxQLConsole.components.result.tabs.profile.ProfileTab',
                'sphinx-console.view.sphinxQLConsole.components.result.tabs.plan.PlanTab',
                'sphinx-console.view.sphinxQLConsole.components.result.tabs.meta.MetaTab'],
    fieldDefaults: {
        labelAlign: 'left',
        msgTarget: 'side'
    },

    initComponent: function(){
        th = this;
        this.items = [
            {
                xtype:'panel',
                margin: "20 0 0 0",
                items:[
                    {
                        xtype:'panel',
                        layout: 'vbox',
                        defaults: {
                            margin: "5 10 5 10"
                        },
                        items:[
                            {
                                xtype:'resultQueryLabel',
                                serverAndPort: th.serverAndPort,
                                queryText: th.queryText
                            },
                            {
                                xtype:'label',
                                text: 'Кол-во результатов: ' + th.resultCount +',  Время выполнения: ' + th.queryExecutionTime
                            }
                        ]

                    },
                    {
                        xtype:'tabpanel',
                        items:[
                            {
                                xtype:'resultTab',
                                layout:'auto',
                                title:'Результаты'
                            },
                            {
                                xtype:'profileTab',
                                layout:'auto',
                                title:'Профиль'
                            },
                            {
                                xtype:'planTab',
                                layout:'auto',
                                title:'План'
                            },
                            {
                                xtype:'metaTab',
                                layout:'auto',
                                title:'Мета'
                            }
                        ]
                    }
                ]
            }
        ];
        this.callParent(arguments);
    }
});