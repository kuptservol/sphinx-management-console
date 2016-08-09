Ext.define('sphinx-console.view.collection.wizard.DistributedCollectionAgentsPanel', {
    extend:'Ext.form.Panel',
    alias : 'widget.distributedCollectionAgentsPanel',
//    autoScroll: true,
    collectionName: null,
    agentsData: null,

    initComponent:function () {
        var th = this;
        th.items = [
            {
                xtype:'panel',
                itemId: 'collectionPanel',
                margin: '10 0 10 0',
                items:[
                    {
                        xtype: 'image',
                        src: 'app/resources/images/remove.png',
                        listeners: {
                            el: {
                                click: function() {
                                    th.findParentByType('stepDistributed2').removeAgentsPanel(th);
                                }
                            }
                        }
                    },
                    {
                        xtype: 'label',
                        text: th.collectionName,
                        style: 'font-weight: bold; text-align:center',
                        margin: '10 10 10 10'
                    }
                ]
            },
            {
                xtype:'panel',
                itemId: 'allAgentsPanelId',
                layout: 'hbox',
                defaults: {
                    margin: '10 10 10 10'
                }
            }
        ];
        this.callParent(arguments);
        var agents = this.agentsData;
        var allAgentsPanel = th.getComponent('allAgentsPanelId');
        for (var i = 0; i < agents.length; i++) {
            this.addReplicaDistributedAgentTextField(allAgentsPanel, agents[i].nodeHost, agents[i].nodeDistribPort);
        }
    },

    addReplicaDistributedAgentTextField: function(panel, host, port){
        var replicaDistributedAgentTextField = Ext.create('Ext.form.field.Text',
            {
                value: host + " : " + port,
                grow: true,
                width:200,
                fieldStyle: 'text-align: center;',
                readOnly: true})
        panel.add(replicaDistributedAgentTextField);
    }

});