Ext.define('sphinx-console.view.collection.details.search.List', {
        extend: 'Ext.grid.Panel',
        itemId: 'replicasList',
        id: 'replicasList',
        alias: 'widget.replicasList',
        store: 'ReplicasData',
        forceFit: true,          //Fit to container
        columnLines: true,
        minHeight: 100,
        maxHeight: 550,
        viewConfig : {
            scroll:false,
            style:{overflow: 'auto',overflowX: 'hidden'}
        },
        autoScroll: false,
        requires: ['sphinx-console.view.collection.details.search.AddReplicaWindow'],
        autoResizeColumns: true,
        wizard: null,

        indexServerHandler: function(collectionName, serverStatus) {
            var action = serverStatus ? 'stopIndexing' : 'startIndexing';
            sphinx-console.app.fireEvent(action, collectionName);
        },

        searchServerHandler: function(collectionName, serverStatus, replicaNumber) {
            var action = serverStatus ? 'stopProcess' : 'startProcess';
            sphinx-console.app.fireEvent(action, collectionName, replicaNumber);
        },

        initComponent: function () {
            this.columns =
                [
                    {
                    xtype:'actioncolumn',
                    header: 'Действия',
                   
                    align: 'center',
                    items: [
                            {
                                icon: 'app/resources/images/log.png',
                                tooltip: 'Лог',
                                handler: function(grid, rowIndex) {
                                    var rec = grid.getStore().getAt(rowIndex);
                                    sphinx-console.app.fireEvent('getLogReplica', rec.get('name'), rec.data.replicaNumber);
                                }
                            },
                            {
                                icon: 'app/resources/images/sphinx_log.png',
                                tooltip: 'sphinx лог',
                                handler: function(grid, rowIndex) {
                                    var rec = grid.getStore().getAt(rowIndex);
                                    
                                    sphinx-console.app.fireEvent('getSphinxLogReplica', rec.data.collectionName, rec.data.replicaNumber);
                                }
                            },
                            {
                                icon: 'app/resources/images/process.png',
                                tooltip: 'Конфигурация',
                                handler: function (grid, rowIndex) {
                                    var rec = grid.getStore().getAt(rowIndex);
                                    sphinx-console.app.fireEvent('getConfigurationCollection', rec.data.collectionName, rec.data.replicaNumber);
                                }
                            },
                            {
                                icon: 'app/resources/images/delete.png',
                                tooltip: 'Удалить',
                                handler: function (grid, rowIndex, colIndex) {
                                    Ext.MessageBox.buttonText.yes = "Да";
                                    Ext.MessageBox.buttonText.no = "Нет";
                                    Ext.MessageBox.confirm('Удаление ноды', 'Удалить ноду?', function(btn){
                                        if(btn === 'yes'){
                                            var rec = grid.getStore().getAt(rowIndex);
                                            var replica = new Object();
                                            replica.collectionName = rec.data.collectionName;
                                            replica.replicaNumber = rec.data.replicaNumber;
                                            grid.up().onDeleteReplica(replica);
                                        }
                                    });
                                }
                            }
                        ]
                    },
                    {
                        header: 'Статус',
                        xtype: 'actioncolumn',
                        align: 'center',
                        items: [
                            {
                                getClass: function (value, meta, record) {
                                    return record.searchLoading ? 'tableImage flagLoading' : record.get('searchServerStatus') ? 'flagRunning' : 'tableImage flagStopped';
                                }
                            }
                        ]
                    },
                    {
                        header: 'Управление',
                        xtype: 'actioncolumn',
                        align: 'center',
                        items: [
                            {
                                getClass: function (value, meta, record) {
                                    var toolTipText = record.get('searchServerStatus') ? 'Остановить' : 'Запустить';
                                    meta.tdAttr = 'data-qtip="' + toolTipText + '"';
                                    return record.searchLoading ? 'tableImage flagLoading' : record.get('searchServerStatus') ? 'tableImage stopbutton' : 'tableImage startbutton';
                                },
                                handler: function (grid, rowIndex) {
                                    var rec = grid.getStore().getAt(rowIndex);
                                     
                                    if(!rec.searchLoading) {
                                        grid.up().searchServerHandler(rec.get('collectionName'), rec.get('searchServerStatus'), rec.get('replicaNumber'));
                                    }
                                    rec.searchLoading = true; rec.commit();
                                }
                            }
                        ]
                    },
                    {
                        header: 'Сервер',
                        xtype: 'templatecolumn',
                        align: 'center',
                        tpl: '<a href="#" onclick="onServerClick(\'{server.name}\', \'{server.ip}\')">{server.name}</a>'
                    },
                    {
                        header: 'Порт',
                        xtype: 'templatecolumn',
                        align: 'center',
                        tpl: '<table><tr><td>Поиск: {searchPort}</td><td rowspan="2"><image src="app/resources/images/edit.png" height="24"  width="24" style="margin-left: 5px" onclick="onModifyCompositePortClick(\'{collectionName}\', \'{replicaNumber}\', \'{searchPort}\', \'{distributedPort}\', \'{collectionType}\')"></td></tr>' +
                        	'<tpl if="collectionType == \'SIMPLE\'"><tr><td>Агент: {distributedPort}</td></tr></tpl></table>' 
                        	 
                    }
                ];
                this.dockedItems = [{
                    xtype: 'toolbar',
                    items: [{
                        itemId: 'refreshReplicas',
                        xtype: 'button',
                        text: 'Обновить',
                        handler : function() {
                            this.up('grid').getView().refresh();
                        }
                    }, '-',{
                        itemId: 'addReplica',
                        text: 'Добавить ноду',
                        handler : function() {
                                                  	
                        	if (this.up('window').collectionType == 'SIMPLE') {
                        	    var addReplicaWindow = Ext.create('sphinx-console.view.collection.details.search.AddReplicaWindow');
                                addReplicaWindow.collectionName = this.up('window').collectionName;
                                addReplicaWindow.show();
                        	} else {
                        		var addDistributedReplicaWindow = Ext.create('sphinx-console.view.collection.details.search.AddDistributedReplicaWindow');
                                addDistributedReplicaWindow.collectionName = this.up('window').collectionName;
                                addDistributedReplicaWindow.show();
                        	}
                        }
                    }]
                },{
                    id: 'replicasPagingToolBar',
                    xtype: 'pagingtoolbar',
                    store: 'ReplicasData',
                    dock: 'bottom',
                    plugins: [new sphinx-console.view.PageSizePlugin()],
                    displayMsg  : 'Записи {0} - {1} из {2}',
                    beforePageText: 'Страница',
                    afterPageText: 'из {0}',
                    refreshText: 'Обновить',
                    displayInfo: true
                }];
            var window = this.up('window');
            var replicasStore = Ext.getStore('ReplicasData');
            replicasStore.setCollectionName(window.collectionName);
            replicasStore.load();
            this.callParent(arguments);
        },
        onDeleteReplica: function(replica) {
            sphinx-console.app.fireEvent('deleteReplica', replica);
//            var uid = sphinx-console.app.getsphinx-consoleControllerReplicasController().onDeleteReplica(replica);
//            Ext.Msg.show({
//                scope: this,
//                title:'Подтверждение',
//                msg: 'Проследить за ходом выполнения задания?',
//                buttonText: {
//                    yes: 'Да',
//                    no: 'Нет'
//                },
//                buttons: Ext.Msg.YESNO,
//                icon: Ext.Msg.QUESTION,
//                fn: function(buttonId, text, opt) {
//                    switch (buttonId) {
//                        case 'yes':
//                            Ext.create('sphinx-console.view.logs.LogWindow', {uid: uid}).show();
//                            break;
//                        case 'no':
//                            break;
//                    }
//                }
//            });
        }
});

function onServerClick(name, ip) {
    Ext.create('sphinx-console.view.servers.ServerViewForm',{name: name, ip: ip}).show();
}

function onModifyCompositePortClick(collectionName, replicaNumber, port, distribPort, collectionType) {
    if (collectionType == 'SIMPLE') {
    	Ext.create('sphinx-console.view.collection.details.search.ModifyReplicaPortWindow', {collectionName: collectionName, replicaNumber: replicaNumber, port: port, distribPort: distribPort}).show();	
    } else {
    	Ext.create('sphinx-console.view.collection.details.search.ModifyDistributedReplicaPortWindow', {collectionName: collectionName, replicaNumber: replicaNumber, port: port}).show();
    }
}

function onModifyPortClick(collectionName, replicaNumber, port, distribPort) {
    Ext.create('sphinx-console.view.collection.details.search.ModifyReplicaPortWindow', {collectionName: collectionName, replicaNumber: replicaNumber, port: port, distribPort: distribPort}).show();
}

function onModifyDistributedReplicaPortClick(collectionName, replicaNumber, port) {
    Ext.create('sphinx-console.view.collection.details.search.ModifyDistributedReplicaPortWindow', {collectionName: collectionName, replicaNumber: replicaNumber, port: port}).show();
}
