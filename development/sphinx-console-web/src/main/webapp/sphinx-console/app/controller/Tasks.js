Ext.define('sphinx-console.controller.Tasks', {
    extend: 'Ext.app.Controller',
    itemId: 'controllerTasks',
    alias: 'widget.tasksController',
    stores: ['Tasks'],
    refs: [
        {
            ref: 'tasksList',
            selector: 'tasksList'
        }
    ],

    init: function () {
        this.application.on('stopTask', this.onStopTask, this);
        this.control({
            '#refreshTasks': {
                click: this.onRefreshTasks
            },
            '#searchTasks':{
                click: this.onSearchTasks
            },
            '#resetSearchTasks':{
                click: this.onResetSearchTasks
            },
            '#tasksTab': {
                activate: function() {
                	 var tasksStore = this.getTasksStore();
                     tasksStore.load({params:{start:0, limit:tasksStore.pageSize}});
              		
                },
                deactivate: function() {
                    
                }
            }
           });
        
    },

    onLaunch: function () {
    	/* var tasksStore = this.getTasksStore();
        tasksStore.load({
            callback: this.onTasksLoad,
            scope: this
        });*/
    },

    onTasksLoad: function (records, callback, successful, opts) {
        var tasksList = this.getTasksList;
        
    },

    onRefreshTasks: function (store) {
    	store.reload();
    },

    

    onSearchTasks: function(searchButton){
        

    },
    onResetSearchTasks: function(searchButton){
            
    },
    
    onLoadByParam: function() {
    	this.getTasksStore().loadPage(1);
    },
    
    onLoad: function() {
        this.getTasksStore().load();
     },
    
    onTasksLoad: function (records, callback, successful, opts) {
        
    },
    
    onPauseTask: function (taskUid, store) {
    	Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/pauseTask',
            method: 'POST',
            useDefaultXhrHeader: false,
            headers: { 'Content-Type': 'application/json' },
            jsonData: {
                "taskUID": taskUid
            },
            success: function(response, opts) {
           	 
           	 var status = Ext.JSON.decode(response.responseText);

           	 if (status && status.code != 0) {
           	     Ext.MessageBox.alert('Ошибка', 
           	    		 'Ошибка, интерфейс: ' + status.systemInterface +
           	    		 ", message: " + status.message +
           	    		 ", description: " + status.description +
           	    		 ", StackTrace: " + status.stackTrace);
           	 } else {
           		store.reload({params:{start:0, limit:store.pageSize}});	                                    		 
           	 }
            },
            failure: function(error) {
           	 Ext.Msg.alert('ERROR OCCURED WHILE EXECUTONG PAUSE TASK: ' + error);
            }
        });

    },
    
    onStopTask: function (taskUid, store) {
    	Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/stopTask',
            method: 'POST',
            useDefaultXhrHeader: false,
            headers: { 'Content-Type': 'application/json' },
            jsonData: {
                "taskUID": taskUid
            },
            success: function(response, opts) {
           	 
           	 var status = Ext.JSON.decode(response.responseText);

           	 if (status && status.code != 0) {
           	     Ext.MessageBox.alert('Ошибка', 
           	    		 'Ошибка, интерфейс: ' + status.systemInterface +
           	    		 ", message: " + status.message +
           	    		 ", description: " + status.description +
           	    		 ", StackTrace: " + status.stackTrace);
           	 } else {
           		if(store) {
                    store.reload({params:{start:0, limit:store.pageSize}});
                }
           	 }
            },
            failure: function(error) {
           	 Ext.Msg.alert('ERROR OCCURED WHILE EXECUTONG STOP TASK: ' + error);
            }
        });

    },
    
    onStartTask: function (taskUid, store) {
    	Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/resumeTask',
            method: 'POST',
            useDefaultXhrHeader: false,
            headers: { 'Content-Type': 'application/json' },
            jsonData: {
                "taskUID": taskUid
            },
            success: function(response, opts) {
           	 
           	 var status = Ext.JSON.decode(response.responseText);

           	 if (status && status.code != 0) {
           	     Ext.MessageBox.alert('Ошибка', 
           	    		 'Ошибка, интерфейс: ' + status.systemInterface +
           	    		 ", message: " + status.message +
           	    		 ", description: " + status.description +
           	    		 ", StackTrace: " + status.stackTrace);
           	 } else {
           		store.reload({params:{start:0, limit:store.pageSize}});	                                    		 
           	 }
            },
            failure: function(error) {
           	 Ext.Msg.alert('ERROR OCCURED WHILE EXECUTONG TASK: ', error);
            }
        });

    },




})