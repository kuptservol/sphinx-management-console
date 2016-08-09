Ext.define('sphinx-console.view.collection.details.search.Window', {
    extend: 'Ext.window.Window',
    width: 900,
    height: 400,
    layout: 'fit',
    resizable: true,
    maximizable: false,
    closable: true,
    closeAction: 'destroy',
    modal: false,
    collectionName: null,
    collectionType: null,
    replicasTaskRunner: Ext.create('sphinx-console.util.ReplicasTaskRunner'),
    requires: ['sphinx-console.view.collection.details.search.List', 'sphinx-console.view.collection.details.search.AddReplicaWindow','sphinx-console.util.ReplicasTaskRunner'],

    constructor: function(collectionName, collectionType) {
        this.collectionName = collectionName;
        this.collectionType = collectionType;
        this.superclass.constructor.call(this);
        this.title = 'Поиск по коллекции ' + collectionName;
    },

    listeners: {
        close: function() {
            this.replicasTaskRunner.destroy();
        }
    },
    tools: [
        {
            type: 'restore',
            hidden : true,
            handler: function(evt, toolEl, owner, tool) {
                var window = owner.up( 'window' );
                window.expand('', false);
                window.setWidth(winWidth);
                window.center();
                isMinimized = false;
                this.hide();
                this.nextSibling().show();
            }
        },{
            type: 'minimize',
            handler: function(evt, toolEl, owner, tool){
                var window = owner.up('window');
                window.collapse();
                winWidth = window.getWidth();
                window.setWidth(150);
                window.alignTo(Ext.getBody(), 'bl-bl');
                this.hide();
                this.previousSibling().show();
                isMinimized = true;
            }
        }
    ],
    initComponent: function () {
        this.items = [
            {
                xtype: 'replicasList'
            }
        ];
        this.callParent(arguments);
        this.replicasTaskRunner.startTask();
    }

});