Ext.define('sphinx-console.view.collection.wizard.StepDistributed2', {
    extend: 'Ext.form.Panel',
    alias: 'widget.stepDistributed2',
    title: 'Выбор коллекций',
    trackResetOnLoad: true,
    requires: ['sphinx-console.view.collection.wizard.DistributedCollectionNodesPanel'],
    layout: 'border',
    collectionName: null,
    simpleCollectionsStore: Ext.create('sphinx-console.store.SimpleCollections'),
    availableCollectionsStore: Ext.create('sphinx-console.store.SimpleCollectionName', {trackRemoved: false}),
    resultStore: Ext.create('sphinx-console.store.SimpleCollections', {trackRemoved: false}),
    margin: 20,

    listeners: {
        validitychange: function(form, valid, eOpts){
            this.up('wizard').fireEvent('activeFormValidityChange');
        }
    },

    initComponent: function(){
        var th = this;
        this.resultStore.removeAll();
        this.updateSimpleCollectionsStore();
        this.items = [
            {
                xtype:'panel',
                region: 'north',
                height: '200',
                items:[
                    {
                        xtype:'label',
                        text:'Распределённая коллекция' + (th.collectionName != null ? ' ' + th.collectionName : '') + ':',
                        width:'100%'
                    }
                ]
            },
            {
                xtype: 'distributedCollectionNodesPanel',
                itemId: 'distributedCollectionNodesPanelItemId',
                region: 'center',
                autoScroll: true,
                simpleCollectionsStore: th.simpleCollectionsStore,
                availableCollectionsStore: th.availableCollectionsStore,
                resultStore: th.resultStore
            },
            {
                xtype: 'panel',
                itemId: 'controlsPanel',
                region: 'south',
                height: '200',
                items: [
                    {
                        xtype:'button',
                        text:'Обновить значениями из конфигурации',
                        handler:function (btn) {
                            if(th.collectionName != null){
                                btn.up('stepDistributed2').reloadDistributedCollectionAgentsPanel(th.collectionName);
                            }
                        }
                    },
                    {
                        xtype: 'panel',
                        itemId: 'collectionChoosePanel',
                        layout: 'hbox',
                        items: [
                            {
                                xtype: 'combo',
                                itemId: 'collectionNameCombo',
                                width:500,
                                displayField: 'collectionName',
                                editable: false,
                                store: th.availableCollectionsStore
                            },
                            {
                                xtype:'button',
                                text:'Добавить',
                                handler:function (btn) {
                                    var collectionNameCombo = btn.up('panel').getComponent('collectionNameCombo');
                                    var collectionName = collectionNameCombo.getValue();
                                    
                                    
                                    var agents = th.simpleCollectionsStore.findRecord('collectionName', collectionName).getData(true).agents;
                                    
                                    
                                    th.getComponent('distributedCollectionNodesPanelItemId').addAgentsPanel(collectionName, agents);
                                    th.removeAlreadyUsedCollectionsFromAvailableCollectionsStore();
                                    collectionNameCombo.setValue(null);
                                    th.resultStoreCountUpdate();
                                }
                            }
                        ]
                    }
                ]
            },
            {xtype: 'numberfield', hidden: 'true', itemId: 'resultStoreCount', minValue : 1, value: 0, allowBlank: false}
        ];
        this.callParent(arguments);
    },

    updateSimpleCollectionsStore: function(){

        var th = this;
        Ext.create('sphinx-console.store.SimpleCollections').load({
            callback: function(records){
                th.simpleCollectionsStore.removeAll();
                for(var i=0; i < records.length; i++){
                    th.simpleCollectionsStore.add(Ext.create('sphinx-console.model.SimpleCollectionWrapper',records[i].getData(true)));
                }
                th.initCollectionNameComboStore();
            },
            scope: this
        });
    },

    reloadDistributedCollectionAgentsPanel:function (collectionName) {

        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/distributedCollectionExtendedInfo/'+collectionName,
            method: 'GET',
            success: function(response) {
                var distributedCollectionNodesData = Ext.JSON.decode(response.responseText);
                th.getComponent('distributedCollectionNodesPanelItemId').rebuildFromData(distributedCollectionNodesData);
            }
        });
    },

    initCollectionNameComboStore: function(){
        var th = this;
        this.availableCollectionsStore.removeAll();
        this.simpleCollectionsStore.each(function(record){
            th.availableCollectionsStore.add(Ext.create('sphinx-console.model.CollectionName',{collectionName: record.data.collectionName}));
        });
        this.removeAlreadyUsedCollectionsFromAvailableCollectionsStore();
    },

    removeAgentsPanel: function(panel){
        this.availableCollectionsStore.add(Ext.create('sphinx-console.model.CollectionName',{collectionName: panel.collectionName}));
        this.resultStore.remove(this.resultStore.findRecord('collectionName', panel.collectionName));
        this.resultStoreCountUpdate();
        panel.destroy();
    },

    getData : function(){
        return this.resultStore.data.items;
    },

    loadData: function(distributedCollectionNodesData) {
        this.getComponent('distributedCollectionNodesPanelItemId').rebuildFromData(distributedCollectionNodesData);
        this.resultStoreCountUpdate();
    },

    resultStoreCountUpdate: function () {
        this.down('#resultStoreCount').setValue(this.resultStore.getCount());
        this.fireEvent('validitychange');
    },

    removeAlreadyUsedCollectionsFromAvailableCollectionsStore: function(){
        var th = this;
        this.resultStore.each(function(record){
            th.availableCollectionsStore.remove(th.availableCollectionsStore.findRecord('collectionName',record.getData(true).collectionName));
        });
    }

});