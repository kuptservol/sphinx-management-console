Ext.define('sphinx-console.controller.Replicas', {
    extend: 'Ext.app.Controller',
    alias: 'widget.replicaController',
    stores: ['ReplicasData'],
    requires: ['sphinx-console.util.Utilities','sphinx-console.view.logs.LogWindowDialog'],
    refs: [
        {
            ref: 'replicasList',
            selector: 'replicasList'
        }
    ],
    logWindowDialog: Ext.create('sphinx-console.view.logs.LogWindowDialog'),

    init: function () {
        if (sphinx-console.util.Utilities.DEBUG)
            console.log("CONTROLLER INIT");
        this.control({
            '#refreshReplicas': {
                click: this.onRefreshReplicas
            }
        });

        this.application.on('getLogReplica', this.onGetLogReplica, this);
        this.application.on('getConfigurationReplica', this.onGetConfigurationReplica, this);
        this.application.on('createReplica', this.onCreateReplica, this);
        this.application.on('createDistributedReplica', this.onCreateDistributedReplica, this);
        this.application.on('successCreateReplica', this.onSuccessCreateReplica, this);
        this.application.on('successCreateReplica', this.onSuccessCreateReplica, this);
        this.application.on('modifyReplicaPort', this.onModifyReplicaPort, this);
        this.application.on('modifyDistributedReplicaPort', this.onModifyDistributedReplicaPort, this);
        this.application.on('deleteReplica', this.onDeleteReplica, this);
        this.application.on('successDeleteReplica', this.onSuccessDeleteReplica, this);
        this.application.on('updateReplicas', this.onRefreshReplicas, this);
    },

    onGetLogReplica: function () {
        
    },

    onGetConfigurationReplica: function () {
        
    },

    onCreateReplica: function (collectionName, server, serverPort, distribPort) {
        var data = new Object();
        data.collectionName = collectionName;
        data.server = server;
        data.searchPort = serverPort;
        data.distributedPort = distribPort;
        var configuration = new Object();
        configuration.url = sphinx-console.util.Utilities.SERVER_URL + '/configuration/replica/create';
        configuration.params = Ext.JSON.encodeValue(data, '\n');
        configuration.onSuccessEventName = 'successCreateReplica';
        var taskUid = this.logWindowDialog.show(configuration);
        return taskUid;
    },
    
    onCreateDistributedReplica: function (collectionName, server, serverPort) {
        var data = new Object();
        data.collectionName = collectionName;
        data.server = server;
        data.searchPort = serverPort;
        var configuration = new Object();
        configuration.url = sphinx-console.util.Utilities.SERVER_URL + '/configuration/replica/distributedCreate';
        configuration.params = Ext.JSON.encodeValue(data, '\n');
        configuration.onSuccessEventName = 'successCreateReplica';
        var taskUid = this.logWindowDialog.show(configuration);
        return taskUid;
    },

    onSuccessCreateReplica: function () {
        this.onRefreshReplicas();
    },

    onModifyReplicaPort: function (collectionName, replicaNumber, serverPort, distribPort) {
        var data = new Object();
        data.collectionName = collectionName;
        data.replicaNumber = replicaNumber;
        data.searchPort = serverPort;
        data.distributedPort = distribPort;
        var configuration = new Object();
        configuration.url = sphinx-console.util.Utilities.SERVER_URL + '/configuration/replica/modifyPort';
        configuration.params = Ext.JSON.encodeValue(data, '\n');
        var taskUid = this.logWindowDialog.show(configuration);
        return taskUid;
    },
    
    onModifyDistributedReplicaPort: function (collectionName, replicaNumber, serverPort) {
        var data = new Object();
        data.collectionName = collectionName;
        data.replicaNumber = replicaNumber;
        data.searchPort = serverPort;
        var configuration = new Object();
        configuration.url = sphinx-console.util.Utilities.SERVER_URL + '/configuration/replica/modifyDistributedReplicaPort';
        configuration.params = Ext.JSON.encodeValue(data, '\n');
        var taskUid = this.logWindowDialog.show(configuration);
        return taskUid;
    },

    onDeleteReplica: function (replica) {
        var th = this;
        var taskUid = null;

        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/last/replica/' + replica.collectionName,
            method: 'POST',
            callback: function(options, success, response) {
                if (success) {
                    var response = Ext.JSON.decode(response.responseText);
                    
                    if(!response.result) {
                        var configuration = new Object();
                        configuration.url = sphinx-console.util.Utilities.SERVER_URL + "/configuration/replica/remove/";
                        configuration.params = Ext.JSON.encodeValue(replica, '\n');
                        configuration.onSuccessEventName = 'successDeleteReplica';
                        taskUid = th.logWindowDialog.show(configuration);
                    } else {
                    	 Ext.MessageBox.alert('Ошибка', 'Невозможно удалить единственную ноду поиска');	
                    }
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
            }
        });

        return taskUid;
    },

    onSuccessDeleteReplica: function () {
        this.onRefreshReplicas();
    },

    onRefreshReplicas: function () {
        this.getReplicasDataStore().reload();
    },

    showFailureResponseWindow: function(status) { //TODO from collection controller
        Ext.MessageBox.alert('Ошибка',
                'Ошибка, интерфейс: ' + status.systemInterface +
                ", message: " + status.message +
                ", description: " + status.description +
                ", StackTrace: " + status.stackTrace);
    }

});