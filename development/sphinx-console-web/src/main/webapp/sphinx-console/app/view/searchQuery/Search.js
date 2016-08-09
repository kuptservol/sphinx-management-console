Ext.define('sphinx-console.view.searchQuery.Search', {
    extend: 'Ext.form.Panel',
    alias: 'widget.searchQuerySearch',
    id: 'searchQuerySearchPanel',
    th: this,
    items:[
        {
            xtype : 'fieldset',
            title : 'Параметры поиска',
            layout: 'container',
            layout : 'hbox',
            height: '100%',
            items: [
                {
                    xtype : 'container',
                    width: '45%',
                    defaults: {
                        labelWidth: 205,
                        width: '100%',
                        margin: 5
                    },
                    items :[
                        {
                            xtype: 'combo',
                            fieldLabel: 'Коллекция',
                            itemId: 'searchQueryCollectionNameCombo',
                            displayField: 'collectionName',
                            editable: false,
                            store: Ext.create('sphinx-console.store.CollectionName'),
                            emptyText: 'Все',
                            listeners: {
                                select: function(combo, record, eOpts) {
                                    var serverAndPortCombo = combo.up('container').getComponent('serverAndPortComboId');
                                    var data = combo.getStore().getById(combo.getValue()).data;
                                    serverAndPortCombo.setStore(Ext.create('sphinx-console.store.ReplicasData', {collectionName: data.collectionName}).load());
                                    serverAndPortCombo.setValue(null);
                                }
                            }
                        },
                        {
                            xtype: 'combo',
                            fieldLabel: 'Реплика',
                            itemId: 'serverAndPortComboId',
                            displayField: 'serverAndPort',
                            emptyText: 'Все',
                            editable: false
                        },
                        {
                            xtype : 'container',
                            layout : 'hbox',
                            items :[
                                {
                                    xtype : 'datefield',
                                    itemId : 'fromDate',
                                    value: new Date(),
                                    margin: '0 5 0 0',
                                    fieldLabel : 'Дата и время выполнения c:',
                                    labelSeparator: '',
                                    labelWidth: 205,
                                    width: 374,
                                    format : 'd.m.Y'
                                },
                                {
                                    xtype: 'timefield',
                                    itemId : 'fromTime',
                                    format: 'H:i',
                                    increment: 30,
                                    width: 120
                                },
                                {
                                    xtype : 'datefield',
                                    itemId : 'toDate',
                                    value: new Date(),
                                    margin: '0 5 0 10',
                                    fieldLabel : 'по',
                                    labelWidth: 20,
                                    width: 185,
                                    labelSeparator: '',
                                    format : 'd.m.Y'
                                },
                                {
                                    xtype: 'timefield',
                                    itemId : 'toTime',
                                    format: 'H:i',
                                    increment: 30,
                                    width: 120
                                }
                            ]
                        },
                        {
                            xtype: 'button',
                            text : 'Найти',
                            cls : 'searchbutton',
                            handler: function(){
                                Ext.getCmp('searchQuerySearchPanel').findSearchQueries();
                            }
                        },
                        {
                            xtype: 'button',
                            text : 'Очистить фильтр',
                            cls : 'clearfilterbutton',
                            handler: function(searchButton) {
                                searchButton.up('form').getForm().reset();
                            }
                        }
                    ]
                },
                {
                    xtype : 'container',
                    flex: 1,
                    margin: '0 0 0 40',
                    defaults: {
                        margin: 5
                    },
                    items :[
                        {
                            xtype : 'container',
                            layout : 'hbox',
                            width: '100%',
                            items :[
                                {
                                    xtype : 'container',
                                    layout : 'hbox',
                                    width: '25%',
                                    items :[
                                        {
                                            fieldLabel: 'Дольше',
                                            xtype : 'textfield',
                                            itemId : 'totalTimeMin',
                                            labelWidth: 80,
                                            width: 150
                                        },
                                        {
                                            xtype : 'label',
                                            margin: '5 0 0 20',
                                            text: ' мc'
                                        }
                                    ]
                                },
                                {
                                    xtype : 'container',
                                    layout : 'hbox',
                                    width: '75%',
                                    items :[
                                        {
                                            fieldLabel: 'Больше',
                                            xtype : 'textfield',
                                            itemId : 'resultCountMin',
                                            labelWidth: 80,
                                            width: 150
                                        },
                                        {
                                            xtype : 'label',
                                            margin: '5 0 0 20',
                                            text: ' рез-ов'
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype : 'container',
                            layout : 'hbox',
                            width: '100%',
                            items :[
                                {
                                    xtype: 'checkbox',
                                    boxLabel: 'Дальше 1-ой страницы',
                                    itemId: 'offsetNotZero',
                                    style: 'vertical-align: middle;'
                                },
                                {
                                    xtype: 'checkbox',
                                    margin: '0 0 0 65',
                                    boxLabel: 'Без результатов',
                                    itemId: 'resultCountZero',
                                    style: 'vertical-align: middle;'
                                }
                            ]
                        }
                    ]
                },
         ]
        }
    ],

    getToDate: function(){
        var result;
        var toDate = Ext.getCmp('searchQuerySearchPanel').down('#toDate').getValue();
        var toTime = Ext.getCmp('searchQuerySearchPanel').down('#toTime').getValue();
        result = sphinx-console.util.Date.getEndOfDayIfTimeEmpty(toDate, toTime);
        return result;
    },

    findSearchQueries: function(){
        var store = Ext.getStore('SearchQueryData');
        store.proxy.setExtraParam('collectionName', Ext.getCmp('searchQuerySearchPanel').down('#searchQueryCollectionNameCombo').getValue());
        var serverAndPort = Ext.getCmp('searchQuerySearchPanel').down('#serverAndPortComboId').getValue();
        if(serverAndPort){
            var serverName = serverAndPort.split(' : ')[0];
            var port = serverAndPort.split(' : ')[1];
            store.proxy.setExtraParam('replicaName', {serverName: serverName, port: port});
        }
        else{
            store.proxy.setExtraParam('replicaName', {serverName: null, port: null});
        }

        var fromDate = Ext.getCmp('searchQuerySearchPanel').down('#fromDate').getValue();
        var fromTime = Ext.getCmp('searchQuerySearchPanel').down('#fromTime').getValue();
        fromDate = sphinx-console.util.Date.addTimeToDate(fromDate, fromTime);
        var toDate = this.getToDate();

        store.proxy.setExtraParam('dateFrom', fromDate ? fromDate.getTime() : null);
        store.proxy.setExtraParam('dateTo', toDate ? toDate.getTime() : null);
        store.proxy.setExtraParam('totalTimeMin', Ext.getCmp('searchQuerySearchPanel').down('#totalTimeMin').getValue());
        store.proxy.setExtraParam('resultCountMin', Ext.getCmp('searchQuerySearchPanel').down('#resultCountMin').getValue());
        store.proxy.setExtraParam('offsetNotZero', Ext.getCmp('searchQuerySearchPanel').down('#offsetNotZero').checked ? true : null);
        store.proxy.setExtraParam('resultCountZero', Ext.getCmp('searchQuerySearchPanel').down('#resultCountZero').checked ? true : null);
        store.load();
    }

});