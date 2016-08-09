Ext.define('sphinx-console.view.sphinxQLConsole.ConsoleWindow', {
    extend : 'Ext.window.Window',
    width: '85%',
    height: '85%',
    layout: 'fit',
    alias: 'widget.consoleWindow',
    maximizable: true,
    modal: true,
    requires: ['sphinx-console.view.sphinxQLConsole.components.ConsolePanel'],
    title: 'SphinxQL консоль',
    bodyPadding: 20,
    collectionName: null,
    queryText: null,
    constructor: function(config){
        this.collectionName = config.collectionName;
        this.title = 'SphinxQL консоль ' + this.collectionName;
        this.callParent(arguments);
    },
    initComponent: function(){
        var th = this;
        this.items = [{
            xtype: 'consolePanel',
            collectionName: th.collectionName,
            queryText: th.queryText
        }];
        this.callParent(arguments);
    }
});