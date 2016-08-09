Ext.define('sphinx-console.view.logs.LogWindow', {
    extend : 'Ext.window.Window',
    width: '85%',
    layout: 'fit',
    alias: 'widget.logWindow',
    id: 'logsWindow',
    maximizable: false,
    modal: true,
    requires: ['sphinx-console.view.logs.Panel'],
    title: 'Просмотр лога по заданию',
    items: [{
        xtype: 'logsPanel'
    }],
    taskRunner: Ext.create('Ext.util.TaskRunner'),
    configuration: null,
    listeners: {
        show: function() {
            if(this.configuration.onShow) {
                var task = new Ext.util.DelayedTask(this.configuration.onShow());
                task.delay(300);
            } //TODO ...
        },
        destroy: function() {
            this.taskRunner.stopAll();
        },
        move: function(in_this, x, y){ 
        	
        } 

    },
    constructor: function (configuration) {
        if(configuration) this.configuration = configuration;
        this.superclass.constructor.call(this);
    },
    initComponent: function() {
        var uid = (this.configuration) ? this.configuration.uid : null;
        var replicaNumber = (this.configuration) ? this.configuration.replicaNumber : null;
        var processId = (this.configuration) ? this.configuration.processId : null;
        var last = (this.configuration) ? this.configuration.last : null;
        var operationType = (this.configuration) ? this.configuration.operationType : null;
        var store = Ext.getStore('TaskLog'); //TODO mb need create
        store.proxy.setExtraParam('taskUid', uid);
        store.proxy.setExtraParam('replicaNumber', replicaNumber);
        store.proxy.setExtraParam('processId', processId);
        store.proxy.setExtraParam('last', last);
        store.proxy.setExtraParam('operationType', operationType);
        store.reload({params:{start:0, limit:10}, extraParam: {taskUid: uid, replicaNumber: replicaNumber, processId: processId,
                        operationType: operationType, last: last}});
        Ext.apply(Ext.getStore('TaskLog'), {pageSize: 10});

        this.taskRunner.start({
            run: function() {
                store.reload();
            },
            interval: 2000
        });
        this.callParent(arguments);
    }
});