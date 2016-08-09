Ext.define('sphinx-console.controller.Servers', {
    extend: 'Ext.app.Controller',
    itemId: 'controllerServers',
    alias: 'controller.servers',
    stores: ['Servers','AdminProcess'],
    requires: ['sphinx-console.util.Utilities', 'sphinx-console.util.ServersTaskRunner','sphinx-console.view.adminProcess.AdminProcess','sphinx-console.view.adminProcess.Window','sphinx-console.util.AdminProcessTaskRunner'],
    serversTaskRunner: null,
    adminProcessPopup: null,
    adminProcessListPopup:null,
    adminProcessTaskRunner: null,
    refs: [
        {
            ref: 'serversList',
            selector: 'serversList'
        }
    ],

    init: function () {
        this.control({
            '#serversTabId': {
                activate: this.onRefreshServers,
                deactivate: function() {
                    if(this.serversTaskRunner) this.serversTaskRunner.destroy();
                }
            }
        });

        if(!this.adminProcessPopup) this.adminProcessPopup = new sphinx-console.view.adminProcess.AdminProcess();
        if(!this.adminProcessListPopup) this.adminProcessListPopup = new sphinx-console.view.adminProcess.Window();

        this.on('updateServerStatusTasks', this.onUpdateServerStatusTasks, this);
        this.application.on('adminProcessesPopup', this.onAdminProcessesPopup, this);
        this.application.on('showAdminProcessPopupList', this.onShowAdminProcessPopupList, this);
        this.application.on('hideAdminProcessPopupList', this.onHideAdminProcessPopupList, this);
        this.application.on('addAdminProcessPopup', this.onAddAdminProcessPopup, this);
        this.application.on('addAdminProcess', this.onAddAdminProcess, this);
        this.application.on('addAdminProcesses', this.onAddAdminProcesses, this);
        this.application.on('editAdminProcessPopup',this.onEditAdminProcessPopup,this);
        this.application.on('updateAdminProcess',this.onUpdateAdminProcess,this);
        this.application.on('removeAdminProcess',this.onRemoveAdminProcess,this);
        this.application.on('updateAdminProcessStatusTasks', this.onUpdateAdminProcessStatusTasks, this);
        this.application.on('refreshAdminProcesses',this.onRefreshAdminProcesses,this);
        this.application.on('refreshServersOnTab',this.onRefreshServers,this);
        this.application.on('updateServer', this.onUpdateServer, this);
    },

    onRefreshServers: function () {
        if(this.serversTaskRunner) this.serversTaskRunner.destroy();
        var serversTaskRunner = new sphinx-console.util.ServersTaskRunner();
        this.getServersStore().loadPage(this.getServersStore().currentPage,{
            callback: function(records){
                serversTaskRunner.startUpdateInfo(records);
            },
            scope: this
        });
        this.serversTaskRunner = serversTaskRunner;
    },

    onUpdateServer: function (idServer,name,ip) {
        var thisController = this;
        Ext.Ajax.request({
            url: thisController.getServersStore().getModel().getProxy().api.create,
            method: 'POST',
            useDefaultXhrHeader: false,
            headers: { 'Content-Type': 'application/json' },
            jsonData: {
                "id": idServer,
                "ip": ip,
                "name": name
            },
            success: function(response, opts) {
                thisController.onRefreshServers();
                sphinx-console.app.fireEvent('afterUpdateServer');
            }
        });
    },

    onSearchServers: function(value){
        var filter = this.getServersStore().getFilters().get('serverName');
        filter.setValue(value?value:null);
        /*if(value&&value.length>0){
            filter.setValue(value);
        }else{
            filter.setValue(/\w+/);
        }*/
    },

    onUpdateServerStatusTasks: function(servers) {

    },

    loadPage: function(number,valueFilterServer){
        this.onSearchServers(valueFilterServer);
        this.getServersStore().loadPage(number);
        this.onRefreshServers();
    },

    previousPage: function(valueFilterServer){
        this.onSearchServers(valueFilterServer);
        this.getServersStore().previousPage();
        this.onRefreshServers();
    },

    nextPage: function(valueFilterServer){
        this.onSearchServers(valueFilterServer);
        this.getServersStore().nextPage();
        this.onRefreshServers();
    },

    onRemoveServer: function(id){
        var thisController = this;
        Ext.Ajax.request({
            url: this.getServersStore().getModel().getProxy().api.remove+"/"+id,
            method: 'DELETE',
            success: function(response, opts) {
                thisController.onRefreshServers();
            }
        });
    },

    onAdminProcessPopupList: function(recordServer){
        this.adminProcessListPopup.setRecordServer(recordServer);
        this.adminProcessListPopup.show();
    },

    onAdminProcessesPopup: function(serverId){
        var filter = this.getAdminProcessStore().getFilters().get('serverId');
        filter.setValue(serverId ? serverId : null);
        this.getAdminProcessStore().loadPage(1);
        sphinx-console.app.fireEvent('successAddAdminProcess');
    },

    onAddAdminProcessPopup: function(recordServer){
        this.adminProcessPopup.setRecordServer(recordServer)
        this.adminProcessPopup.setRecordAdminProcess(null);
        this.adminProcessPopup.show();
    },

    onAddAdminProcess: function(adminProcess){
        var thisController = this;
        Ext.Ajax.request({
            url: this.getAdminProcessStore().getModel().getProxy().api.create,
            method: 'POST',
            useDefaultXhrHeader: false,
            headers: { 'Content-Type': 'application/json' },
            jsonData: Ext.JSON.encodeValue(adminProcess),
            success: function(response, opts) {
                if(thisController.adminProcessListPopup.isVisible()){

                	var status = Ext.JSON.decode(response.responseText);
                    if(status && status.code != 0) {
                    	 Ext.MessageBox.alert('Ошибка: ', status.stackTrace);
                    }
                    thisController.onAdminProcessesPopup(serverId);
                }
            },
            failure: function(error) {
            	 Ext.Msg.alert('Ошибка: ', error);
            }
        });
    },

    onAddAdminProcesses: function(adminProcesses, server) {
        var data = new Object();
        data.adminProcesses = adminProcesses;
        data.server = server;

        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/adminProcesses/add',
            method: 'POST',
            useDefaultXhrHeader: false,
            headers: { 'Content-Type': 'application/json' },
            jsonData: Ext.JSON.encodeValue(data),
            success: function(response, opts) {
                var status = Ext.JSON.decode(response.responseText);
                if(status && status.code != 0) {
                    Ext.MessageBox.alert('Ошибка: ', status.stackTrace);
                }
                sphinx-console.app.getsphinx-consoleControllerServersController().onRefreshServers();
            },
            failure: function(error) {
                Ext.Msg.alert('Ошибка: ', error);
            }
        });
    },

    onEditAdminProcessPopup: function(recordAdminProcess){
        this.adminProcessPopup.setRecordServer(this.adminProcessListPopup.getRecordServer());
        this.adminProcessPopup.setRecordAdminProcess(recordAdminProcess);
        this.adminProcessPopup.show();
    },

    onUpdateAdminProcess: function(type,port,serverId,adminProcessId){
        var thisController = this;
        Ext.Ajax.request({
            url: this.getAdminProcessStore().getModel().getProxy().api.update,
            method: 'PUT',
            useDefaultXhrHeader: false,
            headers: { 'Content-Type': 'application/json' },
            jsonData: {
                "server":{"id":serverId},
                "id":   adminProcessId,
                "type": type,
                "port": port
            },
            success: function(response, opts) {
                if(thisController.adminProcessListPopup.isVisible()){
                	
                	var status = Ext.JSON.decode(response.responseText);
                    if(status && status.code != 0) {
                    	 Ext.MessageBox.alert('Ошибка: ', status.stackTrace);      
                    }
                    
                    thisController.onAdminProcessesPopup(serverId);
                }
            },
            failure: function(error) {
           	 Ext.Msg.alert('Ошибка: ', error);
            }
        });
    },

    onRemoveAdminProcess: function(adminProcessId){
        var thisController = this;
        Ext.Ajax.request({
            url: this.getAdminProcessStore().getModel().getProxy().api.remove+"/"+adminProcessId,
            method: 'DELETE',
            success: function(response, opts) {
                thisController.getAdminProcessStore().loadPage(thisController.getAdminProcessStore().currentPage);
            }
        });
    },

    onShowAdminProcessPopupList: function(serverRecord){
        this.onAdminProcessPopupList(serverRecord);
        this.onRefreshAdminProcesses();
    },

    onHideAdminProcessPopupList: function(){
        if(this.adminProcessTaskRunner) this.adminProcessTaskRunner.destroy();
    },

    onUpdateAdminProcessStatusTasks: function(adminProcesses){
        Ext.Array.each(adminProcesses, function(adminProcess) {
            Ext.Ajax.request({
                url: sphinx-console.util.Utilities.SERVER_URL + '/view/adminProcessStatus',
                method: 'POST',
                useDefaultXhrHeader: false,
                headers: { 'Content-Type': 'application/json' },
                jsonData: {
                    "server":{"ip":adminProcess.data.server.ip,"id":adminProcess.data.server.id},
                    "id":  adminProcess.data.id,
                    "type": adminProcess.data.type,
                    "port": adminProcess.data.port
                },
                success: function(response, opts) {
                    var status = '';
                    switch(response.responseText){
                        case "\"RUNNING\"" : status = "Запущен";break;
                        case "\"STOPPED\"" : status = "Остановлен";break;
                    }
                    adminProcess.data.status=status;
                    record.commit();
                }
            });
        });
    },

    onRefreshAdminProcesses: function () {
        if(this.adminProcessTaskRunner) this.adminProcessTaskRunner.destroy();
        var adminProcessTaskRunner = new sphinx-console.util.AdminProcessTaskRunner();
        this.getAdminProcessStore().loadPage(this.getAdminProcessStore().currentPage,{
            callback: function(records){
                adminProcessTaskRunner.startUpdateInfo(records);
            },
            scope: this
        });
        this.adminProcessTaskRunner = adminProcessTaskRunner;
    }
});