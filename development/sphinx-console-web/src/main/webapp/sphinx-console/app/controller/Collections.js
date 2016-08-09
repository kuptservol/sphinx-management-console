Ext.define('sphinx-console.controller.Collections', {
    extend: 'Ext.app.Controller',
    alias: 'widget.collectionController',
    stores: ['CollectionData', 'Servers','ConfigurationTemplateIndexer','ConfigurationFields','ConfigurationTemplateSearch','ConfigurationTemplateConfiguration'],
    requires: ['sphinx-console.util.Utilities', 'sphinx-console.util.CollectionTaskRunner'],
    refs: [
        {
            ref: 'collectionList',
            selector: 'collectionList'
        }
    ],


    filters: [{id: 'name', property:'name'},
              {id: 'searchServerName',property:'searchServerName'},
              {id: 'indexServerName',property:'indexServerName'}],

    collectionTaskRunner: Ext.create('sphinx-console.util.CollectionTaskRunner'),

    init: function () {
        if (sphinx-console.util.Utilities.DEBUG)
    	    console.log("CONTROLLER INIT");
        this.control({
            '#clearFilterCollections': {
                click: this.onClearFilterCollections
            },
            '#refreshCollections': {
                click: this.onRefreshCollections
            },
            '#collectionTab': {
                activate: function() {
                    this.getCollectionDataStore().load({
                        callback: this.onCollectionsLoad,
                        scope: this
                    });
                },
                deactivate: function() {
                    this.collectionTaskRunner.destroy();
                }
            }
        });

        this.application.on('getLogCollection', this.onGetLogCollection, this);
        this.application.on('getLogReplica', this.onGetLogReplica, this);
        this.application.on('getSphinxLogReplica', this.onGetSphinxLogReplica, this);
        this.application.on('getLogOperation', this.onGetLogOperation, this);
        this.application.on('getConfigurationCollection', this.onGetConfigurationCollection, this);
        this.application.on('getCollectionJson', this.onGetCollectionJson, this);
        this.application.on('getServerJson', this.onGetServerJson, this);
        this.application.on('getSnippetJson', this.onGetSnippetJson, this);
        this.application.on('openConsoleWindow', this.onOpenConsoleWindow, this);
        this.application.on('addCollection', this.onAddCollection, this);
        this.application.on('modifyCollection', this.onModifyCollection, this);
        this.application.on('deleteCollection', this.onDeleteCollection, this);
        this.application.on('changeCollectionUpdateSchedule', this.onChangeCollectionUpdateSchedule, this);
        this.application.on('fullIndexing', this.onFullIndexing, this);
        this.application.on('stopFullIndexing', this.onStopFullIndexing, this);
        this.application.on('applyFullIndexing', this.onApplyFullIndexing, this);
        this.application.on('deleteFullIndexData', this.onDeleteFullIndexData, this)

        this.application.on('updateCollectionSize', this.onUpdateCollectionSize, this);
        this.application.on('updateCollectionSearchStatus', this.onUpdateCollectionSearchStatus, this);
        this.application.on('updateCollectionIndexStatus', this.onUpdateCollectionIndexStatus, this);
        this.application.on('updateCollectionsInfo', this.onUpdateCollectionsInfo, this);
        this.application.on('searchCollections', this.onSearchCollections, this);
        this.application.on('changePageSize', this.onChangePageSize, this);
        this.application.on('startIndexing', this.onStartIndexing, this);
        this.application.on('stopIndexing', this.onStopIndexing, this);
        this.application.on('startProcess', this.onStartProcess, this);
        this.application.on('stopProcess', this.onStopProcess, this);
        this.application.on('startDistributedCollectionReload', this.onStartDistributedCollectionReload, this);
    },

    dataStoreUpdateByCollectionsInfo: function(dataStoreRecords, collectionsInfo) {
        dataStoreRecords.forEach(function (record) {
            var info = collectionsInfo[record.get('name')];
            if (info) {
                record.data.collectionSize = info.collectionSize;
                record.data.searchStatus = info.allProcessStatus == 'ALL_SUCCESS' ? true : false;
                record.data.isIndexing = info.isCurrentlyIndexing;
                record.data.lastIndexingTime = info.lastIndexingTime ? new Date(info.lastIndexingTime) : '';
                record.data.nextIndexingTime = info.nextIndexingTime ? new Date(info.nextIndexingTime) : '';
                record.data.collection.processingFailed = info.processingFailed;
            }
        });
        this.getCollectionList().getView().refresh();
    },

    onUpdateCollectionsInfo: function(infos, shouldBeRefreshed) {
        var store = this.getCollectionDataStore();

        // Проверяем - изменился ли состав коллекций.
        // Если изменился - их надо обновить в гриде.


        var needReload = store.getTotalCount() != Object.keys(infos).length;


        if (!needReload) {
            // Количество прежнее, но может быть это другие коллекции, проверяем на совпадение имен
            store.getRange().forEach(function(record) {
                var info = infos[record.get('name')];
                if (!info) {
                    needReload = true;
                }
            });
        }

        if (needReload || shouldBeRefreshed) {
            store.load({scope: this, callback: function (records, operation, success) {
                this.dataStoreUpdateByCollectionsInfo(records, infos);
            }});
        }
        else {
            this.dataStoreUpdateByCollectionsInfo(store.getRange(), infos);
        }
    },

    onUpdateCollectionSize: function(collectionName, collectionSize) {
            var store = this.getCollectionDataStore();
            var record = store.findRecord('name',collectionName, false, false);
            record.data.collectionSize = collectionSize;
            record.commit();
            //this.getCollectionList().getView().refreshNode(record);
    },

    onUpdateCollectionSearchStatus: function(collectionName, searchStatus) {
        var store = this.getCollectionDataStore();
        var record = store.findRecord('name',collectionName, false, false);
        record.data.searchServerStatus = searchStatus;
        //this.getCollectionList().getView().refreshNode(record);
        record.commit();
    },

    onUpdateCollectionIndexStatus: function(collectionName, indexStatus) {
    	if (sphinx-console.util.Utilities.DEBUG)
    	    console.log('UpdateCollectionIndexStatus: ' + indexStatus + ' for collection: ' + collectionName);
        var store = this.getCollectionDataStore();
        var record = store.findRecord('name',collectionName, false, false);
        record.data.isIndexing = indexStatus;
        record.commit();
    },

    onSearchCollections: function(collectionName,  indexServer, searchServer, pageSize) {
        this.collectionTaskRunner.destroy();
        this.filters[0].value = collectionName;
        this.filters[1].value = searchServer;
        this.filters[2].value = indexServer;
        this.getCollectionDataStore().filter(this.filters);
        this.configureTaskRunner();
    },

    onClearFilterCollections: function() {
        this.collectionTaskRunner.destroy();
        this.getCollectionDataStore().clearFilter();
        this.getCollectionDataStore().reload();
    },

    onChangePageSize: function(pageSize) {
        this.getCollectionDataStore().filter(this.filters);
    },

    onLaunch: function () {
        var collectionsStore = this.getCollectionDataStore();
        collectionsStore.load({
            callback: this.onCollectionsLoad,
            scope: this
        });
    },

    onCollectionsLoad: function () {
        this.configureTaskRunner()
    },

    configureTaskRunner: function() {
        this.collectionTaskRunner.destroy();
        this.collectionTaskRunner = new sphinx-console.util.CollectionTaskRunner();
        this.collectionTaskRunner.startTask();
    },

    onRefreshCollections: function(shouldBeRefreshed) {
        this.getCollectionInfoFromAgent(true);
    },

    getCollectionInfoFromAgent: function(shouldBeRefreshed) {
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/queryCollectionsInfoFromAgent',
            method: 'GET',
            success: function(response) {
                var collectionInfo = Ext.JSON.decode(response.responseText);
                th.onUpdateCollectionsInfo(collectionInfo, shouldBeRefreshed);
            },
            failure: function(error) {
            }
        });
    },

    onFullIndexing: function(collectionName, server) {
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/makeCollectionFullRebuildIndex/' + collectionName + '/' + server.name,
            method: 'POST',
            callback: function(options, success, response) {
                if (success) {
                    var status = Ext.JSON.decode(response.responseText);
                    if(status && status.code != 0) {
                        th.showFailureResponseWindow(status);
                    } else {
                    	if (sphinx-console.util.Utilities.DEBUG)
                            console.log('TASK UID: ' + status.taskUID);
                    }
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
            }
        });

    },

    onStopFullIndexing: function(collectionName, server) {
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/stopFullRebuildIndexing/' + collectionName + '/' + server.name,
            method: 'POST',
            callback: function(options, success, response) {
                if (success) {
                    var status = Ext.JSON.decode(response.responseText);
                    if(status && status.code != 0) {
                        th.showFailureResponseWindow(status);
                    } else {
                        if (sphinx-console.util.Utilities.DEBUG)
                            console.log('TASK UID: ' + status.taskUID);
                    }
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
            }
        });
    },

    onApplyFullIndexing: function(collectionName, server) {
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/makeCollectionFullRebuildApply/' + collectionName + '/' + server.name,
            method: 'POST',
            callback: function(options, success, response) {
                if (success) {
                    var status = Ext.JSON.decode(response.responseText);
                    if(status && status.code != 0) {
                        th.showFailureResponseWindow(status);
                    } else {
                    	if (sphinx-console.util.Utilities.DEBUG)
                            console.log('TASK UID: ' + status.taskUID);
                    }
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
            }
        });
    },

    onDeleteFullIndexData: function(collectionName) {
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/deleteFullIndexData/' + collectionName,
            method: 'POST',
            callback: function(options, success, response) {
                if (success) {
                    var status = Ext.JSON.decode(response.responseText);
                    if(status && status.code != 0) {
                        th.showFailureResponseWindow(status);
                    } else {
                    	if (sphinx-console.util.Utilities.DEBUG)
                            console.log('TASK UID: ' + status.taskUID);
                    }
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
            }
        });
    },

    onStartProcess: function(collectionName, replicaNumber) {
        var th = this;
        var actionUrl;

        if (sphinx-console.util.Utilities.DEBUG)
            console.log('REPLICA NUMBER: ' + replicaNumber);

        if (replicaNumber) {
        	actionUrl = sphinx-console.util.Utilities.SERVER_URL + '/configuration/startProcess/' + collectionName + '/replica/' + replicaNumber;
        } else {
        	actionUrl = sphinx-console.util.Utilities.SERVER_URL + '/configuration/startProcess/' + collectionName;
        }

        Ext.Ajax.request({
            url: actionUrl,
            method: 'POST',
            callback: function(options, success, response) {
                if(success) {
                    var status = Ext.JSON.decode(response.responseText);
                    if (status && status.code != 0) {
                        th.showFailureResponseWindow(status);
                    }
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
                if (replicaNumber) {
                	th.changeSearchLoadingReplicaStatus(collectionName, replicaNumber);
                } else {
                	th.changeSearchLoadingStatus(collectionName);
                	th.collectionTaskRunner.getSearchStatus(collectionName);
                }
            }
        });
    },

    onStopProcess: function(collectionName, replicaNumber) {
        var th = this;
        var actionUrl;

        if (sphinx-console.util.Utilities.DEBUG)
            console.log('REPLICA NUMBER: ' + replicaNumber);

        if (replicaNumber) {
        	actionUrl = sphinx-console.util.Utilities.SERVER_URL + '/configuration/stopProcess/' + collectionName + '/replica/' + replicaNumber;
        } else {
        	actionUrl = sphinx-console.util.Utilities.SERVER_URL + '/configuration/stopProcess/' + collectionName;
        }

        Ext.Ajax.request({
            url: actionUrl,
            method: 'POST',
            callback: function(options, success, response) {
                if(success) {
                    var status = Ext.JSON.decode(response.responseText);
                    if (status && status.code != 0) {
                        th.showFailureResponseWindow(status);
                    }
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
                if (replicaNumber) {
                	th.changeSearchLoadingReplicaStatus(collectionName, replicaNumber);
                } else {
                	th.changeSearchLoadingStatus(collectionName);
                	th.collectionTaskRunner.getSearchStatus(collectionName);
                }
            }
        });
    },

    onStartIndexing: function(collectionName, taskType) {
        var th = this;
        var action = (taskType == 'INDEXING_DELTA') ? 'rebuildCollection/' : 'mergeCollection/';
        th.changeIndexLoadingStatus(collectionName);
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/' + action + collectionName,
            method: 'POST',
            callback: function(options, success, response) {
                if (success) {
                    var status = Ext.JSON.decode(response.responseText);
                    if(status && status.code != 0) {
                          th.showFailureResponseWindow(status);
                    }
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
                th.collectionTaskRunner.getIndexStatus(collectionName);
            }
        });
    },

    onStopIndexing: function(collectionName, taskType) {
        var th = this;
        var action = (taskType == 'INDEXING_DELTA') ? 'stopIndexing/' : 'stopMerging/';
        th.changeIndexLoadingStatus(collectionName);
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/' + action + collectionName,
            method: 'POST',
            callback: function(options, success, response) {
                var status = Ext.JSON.decode(response.responseText);
                if (success) {
                    if(status && status.code != 0) {
                        th.showFailureResponseWindow(status);
                    }
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
                th.collectionTaskRunner.getIndexStatus(collectionName);
            }
        });
    },

    onAddCollection: function(data) {
        this.collectionSaveOrUpdate('/configuration/addCollection', data);
    },

    onModifyCollection: function(data) {
        this.collectionSaveOrUpdate('/configuration/modifyCollectionAttributes', data);
    },

    collectionSaveOrUpdate: function(url, data){
        var th = this;
        Ext.Ajax.request({
            async: false,
            url:  sphinx-console.util.Utilities.SERVER_URL + url,
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            params: Ext.JSON.encodeValue(data, '\n'),
            waitTitle:'Connecting',
            waitMsg:'Creating...',
            method: 'POST',
            success: function(response) {
                var status = Ext.JSON.decode(response.responseText);
                if (status && status.code != 0) {
                    th.showFailureResponseWindow(status)
                }
            },
            failure:function(response, request ) {
                Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
            }

        });
    },

    onGetLogCollection: function(collectionName) {
        var tabs = Ext.getCmp('tabPanelId');
        Ext.getStore('Tasks').proxy.setExtraParam('collectionName', collectionName);
        Ext.getCmp('collectionSearchField').setValue(collectionName);
    	Ext.getStore('Tasks').reload({params:{start:0, limit:Ext.getStore('Tasks').pageSize}});
        tabs.setActiveTab(Ext.getCmp('tabPanelId').down('tasksTab'));
    },

    onGetLogReplica: function(collectionName, replicaNumber) {
        Ext.create('sphinx-console.view.logs.LogWindow', {replicaNumber: replicaNumber}).show();
    },

    onGetSphinxLogReplica: function (collectionName, replicaNumber) {
    	var configContent;
    	var logWindow = Ext.create('Ext.window.Window', {
      		width: 900,
            height: 650,
            layout: 'fit',
            maximizable: false,
            autoScroll:true,
            title: 'sphinx log',


            tbar: [

				'Укажите кол-во записей: ',
				 {
				     xtype     : 'combo',
				     id: 'recordNumberField',
				     width     : 100,
				     value     : 10,
				     store     : [
				         '10',
				         '20',
				         '30',
				         '40',
				         '50',
				         '100',
				         '200',
				         '300',
				         '400',
				         '500'
				     ],
				     listeners: {
                         select: function(combo, records, eOpts) {
                        	 Ext.Ajax.request({
                                 url: sphinx-console.util.Utilities.SERVER_URL + '/view/sphinxLog/' + collectionName + '/' + replicaNumber + '/' + combo.value,

                                 method: 'POST',
                                 loadScripts: true,
                                 success: function(response, opts) {
                                   	configContent = response.responseText;
                                   	htmlReadyConfigContent = configContent.split("\n").join("<br />");
                                    htmlReadyConfigContent = htmlReadyConfigContent.split("{").join('{<div style="margin-left: 20px;">');
                                    htmlReadyConfigContent = htmlReadyConfigContent.split("}").join('</div>}');

                                    logWindow.body.update(htmlReadyConfigContent);

                                    return;
                                    },
                                    failure: function(error) {
                                   	 Ext.Msg.alert('ERROR OCCURED WHILE RETRIEVINS SPHINX CONF: ' + error);
                                    }

                             });

                         }
                     }

				 }

             	],


            bodyStyle:"padding:10 10 0 10px;background:#FFFFFF;background-color:#FFFFFF",


            buttons: [{
                text: 'Загрузить файл',
                handler: function() {
                	var contentType = 'plain/text';
                    var a = document.createElement('a');
                    var blob = new Blob([configContent], {'type':contentType});
                    a.href = window.URL.createObjectURL(blob);
                    a.download = "searchd.log";
                    a.click();
                }
            },{
                text: 'Закрыть',
                handler: function() {
                	logWindow.close();
                }
            }]
        });

        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/sphinxLog/' + collectionName + '/' + replicaNumber + '/' + Ext.getCmp('recordNumberField').value,

            method: 'POST',
            loadScripts: true,
            success: function(response, opts) {
              	configContent = response.responseText;
              	htmlReadyConfigContent = configContent.split("\n").join("<br />");
                htmlReadyConfigContent = htmlReadyConfigContent.split("{").join('{<div style="margin-left: 20px;">');
                htmlReadyConfigContent = htmlReadyConfigContent.split("}").join('</div>}');

                logWindow.html = htmlReadyConfigContent;

              	logWindow.show();
                return;
               },
               failure: function(error) {
              	 Ext.Msg.alert('ERROR OCCURED WHILE RETRIEVINS SPHINX CONF: ' + error);
               }

        });

    },

    onGetLogOperation: function(collectionName, operationType) {
        Ext.create('sphinx-console.view.logs.LogWindow', {last: true, operationType: operationType}).show();
    },

    onOpenConsoleWindow: function(collectionName, queryText) {
        Ext.create('sphinx-console.view.sphinxQLConsole.ConsoleWindow',{collectionName: collectionName, queryText: queryText}).show();
    },

    onGetConfigurationCollection: function (collectionName, replicaNumber) {
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/sphinxConf/' + collectionName + "/" + replicaNumber,

            method: 'POST',
            loadScripts: true,
            success: function(response, opts) {
              	var configContent = response.responseText;
              	htmlReadyConfigContent = configContent.split("\n").join("<br />");
                htmlReadyConfigContent = htmlReadyConfigContent.split("{").join('{<div style="margin-left: 20px;">');
                htmlReadyConfigContent = htmlReadyConfigContent.split("}").join('</div>}');
              	var configWindow = Ext.create('Ext.window.Window', {
              		width: 900,
	                height: 650,
	                layout: 'fit',
	                maximizable: false,
	                autoScroll:true,
	                title: 'sphinx.conf',
	                bodyStyle:"padding:10 10 0 10px;background:#FFFFFF;background-color:#FFFFFF",
	                html: htmlReadyConfigContent,
	                buttons: [
                        {
                            text: 'Загрузить файл',
                            handler: function() {
                                var contentType = 'plain/text';
                                var a = document.createElement('a');
                                var blob = new Blob([configContent], {'type':contentType});
                                a.href = window.URL.createObjectURL(blob);
                                a.download = "sphinx.conf";
                                a.click();
                            }
                        },
                        {
                            text: 'Закрыть',
                            handler: function() {
                                configWindow.close();
                            }
                        }]
	            });
              	configWindow.show();
                return;
            },
            failure: function(error) {
                Ext.Msg.alert('ERROR OCCURED WHILE RETRIEVINS SPHINX CONF: ' + error);
            }
        });
    },

    onGetCollectionJson: function (collectionName) {
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/collectionWrapperJson/' + collectionName,
            method: 'POST',
            loadScripts: true,
            success: function(response, opts) {
                var configContent = response.responseText;
                htmlReadyConfigContent = configContent.split("\n").join("<br />");
                htmlReadyConfigContent = htmlReadyConfigContent.split("{").join('{<div style="margin-left: 20px;">');
                htmlReadyConfigContent = htmlReadyConfigContent.split("}").join('</div>}');
                var configWindow = Ext.create('Ext.window.Window',
                {
                    width: 900,
                    height: 650,
                    layout: 'fit',
                    maximizable: false,
                    autoScroll: true,
                    title: collectionName + ".json",
                    bodyStyle: "padding:10 10 0 10px;background:#FFFFFF;background-color:#FFFFFF",
                    html: htmlReadyConfigContent,
                    buttons: [
                        {
                            text: 'Загрузить файл',
                            handler: function() {
                                var contentType = 'plain/text';
                                var a = document.createElement('a');
                                var blob = new Blob([configContent], {'type':contentType});
                                a.href = window.URL.createObjectURL(blob);
                                a.download = collectionName + ".json";
                                a.click();
                            }
                        },
                        {
                            text: 'Закрыть',
                            handler: function() {
                                configWindow.close();
                            }
                        }]
                });
                configWindow.show();
                return;
            },
            failure: function(error) {
                Ext.Msg.alert('ERROR OCCURRED WHILE RETRIEVING COLLECTION JSON: ' + error);
            }
        });
    },

    onGetServerJson: function (serverName) {
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/serverWrapperJson/' + serverName,
            method: 'POST',
            loadScripts: true,
            success: function(response, opts) {
                var configContent = response.responseText;
                htmlReadyConfigContent = configContent.split("\n").join("<br />");
                htmlReadyConfigContent = htmlReadyConfigContent.split("{").join('{<div style="margin-left: 20px;">');
                htmlReadyConfigContent = htmlReadyConfigContent.split("}").join('</div>}');
                var configWindow = Ext.create('Ext.window.Window',
                    {
                        width: 900,
                        height: 650,
                        layout: 'fit',
                        maximizable: false,
                        autoScroll: true,
                        title: serverName + ".json",
                        bodyStyle: "padding:10 10 0 10px;background:#FFFFFF;background-color:#FFFFFF",
                        html: htmlReadyConfigContent,
                        buttons: [
                            {
                                text: 'Загрузить файл',
                                handler: function() {
                                    var contentType = 'plain/text';
                                    var a = document.createElement('a');
                                    var blob = new Blob([configContent], {'type':contentType});
                                    a.href = window.URL.createObjectURL(blob);
                                    a.download = serverName + ".json";
                                    a.click();
                                }
                            },
                            {
                                text: 'Закрыть',
                                handler: function() {
                                    configWindow.close();
                                }
                            }]
                    });
                configWindow.show();
                return;
            },
            failure: function(error) {
                Ext.Msg.alert('ERROR OCCURRED WHILE RETRIEVING SERVER JSON: ' + error);
            }
        });
    },

    onGetSnippetJson: function (collectionName) {
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/snippetWrapperJson/' + collectionName,
            method: 'POST',
            loadScripts: true,
            success: function(response, opts) {
                var configContent = response.responseText;
                htmlReadyConfigContent = configContent.split("\n").join("<br />");
                htmlReadyConfigContent = htmlReadyConfigContent.split("{").join('{<div style="margin-left: 20px;">');
                htmlReadyConfigContent = htmlReadyConfigContent.split("}").join('</div>}');
                var configWindow = Ext.create('Ext.window.Window',
                    {
                        width: 900,
                        height: 650,
                        layout: 'fit',
                        maximizable: false,
                        autoScroll: true,
                        title: "snippet_" + collectionName + ".json",
                        bodyStyle: "padding:10 10 0 10px;background:#FFFFFF;background-color:#FFFFFF",
                        html: htmlReadyConfigContent,
                        buttons: [
                            {
                                text: 'Загрузить файл',
                                handler: function() {
                                    var contentType = 'plain/text';
                                    var a = document.createElement('a');
                                    var blob = new Blob([configContent], {'type':contentType});
                                    a.href = window.URL.createObjectURL(blob);
                                    a.download = "snippet_" + collectionName + ".json";
                                    a.click();
                                }
                            },
                            {
                                text: 'Закрыть',
                                handler: function() {
                                    configWindow.close();
                                }
                            }]
                    });
                configWindow.show();
                return;
            },
            failure: function(error) {
                Ext.Msg.alert('ERROR OCCURRED WHILE RETRIEVING SNIPPET JSON: ' + error);
            }
        });
    },

    onDeleteCollection: function (collectionName) {
        var th = this;
        var distributedCollections = this.getDistributedCollections(collectionName);
        var collectionNamesString = "";
        if (distributedCollections.length > 0) {
            distributedCollections.forEach(function (element, index, array) {
                if (collectionNamesString != "") collectionNamesString += ",";
                collectionNamesString = collectionNamesString + element.collectionName;
            });
            Ext.MessageBox.buttonText.yes = "Да";
            Ext.MessageBox.buttonText.no = "Нет";
            Ext.MessageBox.confirm('Удаление коллекции', 'Коллекция ' + collectionName + ' содержится в следующих распределенных коллекциях: '
            + collectionNamesString, function (btn) {
                if (btn === 'yes') {
                    th.deleteCollectionRequest(collectionName);
                }
            });
        }
        else{
            th.deleteCollectionRequest(collectionName);
        }

    },

    deleteCollectionRequest: function (collectionName){
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + "/configuration/deleteCollection/" + collectionName,
            method: 'POST',
            success: function (response) {
                var status = Ext.JSON.decode(response.responseText);
                if (status && status.code != 0) {
                    th.showFailureResponseWindow(status);
                }
            }
        });
    },

    getDistributedCollections: function (collectionName) {
        var th = this;
        var distributedCollections = [];

        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + "/view/distributedCollectionNames/" + collectionName,
            method: 'GET',
            async: false,
            success: function (response) {
                distributedCollections = Ext.JSON.decode(response.responseText);
            },
            failure: function (response){
                sphinx-console.util.ErrorMessage.showFailureResponse(response);
            }
        });

        return distributedCollections;
    },

    onChangeCollectionUpdateSchedule: function (data) {
        this.collectionSaveOrUpdate('/configuration/changeCollectionUpdateSchedule', data);
    },

    changeIndexLoadingStatus: function(collectionName) {
        var store = this.getCollectionDataStore();
        var record = store.findRecord('name',collectionName, false, false);
        record.data.isIndexing = !record.data.isIndexing;
        record.commit();
    },

    changeSearchLoadingStatus: function(collectionName) {
        var store = this.getCollectionDataStore();
        var record = store.findRecord('name',collectionName, false, false);
        record.searchLoading = false;
        record.commit();
    },

    changeSearchLoadingReplicaStatus: function(collectionName, replicaNumber) {
        var store = Ext.getStore('ReplicasData');
        var record = store.findRecord('replicaNumber',replicaNumber, false, false);
        var status = record.get('searchServerStatus');

        if (sphinx-console.util.Utilities.DEBUG)
            console.log('STATUS: ' + status);

        if (status) {
        	record.data.searchServerStatus = false;
        } else {
        	record.data.searchServerStatus = true;
        }

        record.searchLoading = false;

        Ext.Function.defer(function(){
        	record.commit();
            }, 2000);

    },

    showFailureResponseWindow: function(status) {
        Ext.MessageBox.alert('Ошибка',
                'Ошибка, интерфейс: ' + status.systemInterface +
                ", message: " + status.message +
                ", description: " + status.description +
                ", StackTrace: " + status.stackTrace);
    },

    onStartDistributedCollectionReload: function(collectionName) {
        var th = this;

        Ext.MessageBox.buttonText.yes = "Да";
        Ext.MessageBox.buttonText.no = "Нет";
        Ext.MessageBox.confirm('Распределенная коллекция', 'Запустить перезагрузку распределенной коллекции?', function(confirmBtn){
            if(confirmBtn === 'yes'){
                Ext.Ajax.request({
                    url: sphinx-console.util.Utilities.SERVER_URL  + '/configuration/reloadDistributedCollection/' + collectionName,
                    method: 'POST',
                    success: function(response) {
                        var status = Ext.JSON.decode(response.responseText);
                        if (status && status.code != 0) {
                            th.showFailureResponseWindow(status)
                        }
                    }
                });
            }
        });
    }
});