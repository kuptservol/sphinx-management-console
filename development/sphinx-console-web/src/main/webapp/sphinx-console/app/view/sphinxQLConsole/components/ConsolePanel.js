Ext.define('sphinx-console.view.sphinxQLConsole.components.ConsolePanel' ,{
    extend: 'Ext.form.Panel',
    alias : 'widget.consolePanel',
    autoScroll: true,
    fieldDefaults: {
        labelAlign: 'left',
        msgTarget: 'side'
    },

    initComponent: function(){
        th = this;
        this.items = [
            {
                xtype:'panel',
                height:200,
                items:[
                    {
                        xtype: 'combo',
                        id: 'serverAndPortComboId',
                        width: 400,
                        fieldLabel: 'Сервер',
                        store: Ext.create('sphinx-console.store.AvailableReplicasData', {collectionName: th.collectionName}),
                        displayField: 'serverAndPort',
                        editable: false
                    },
                    {
                        xtype:'label',
                        text:'SphinxQL:'
                    },
                    {   xtype: 'textarea',
                        id: 'queryTextareaId',
                        height: 80,
                        width: '100%',
                        value: th.queryText ? th.queryText : 'select * from ' + th.collectionName
                    },
                    {
                        xtype:'button',
                        text:'Отправить',
                        handler:function (btn) {
                            var serverName = Ext.getCmp('serverAndPortComboId').getValue().split(' : ')[0];
                            var port = Ext.getCmp('serverAndPortComboId').getValue().split(' : ')[1];
                            var query = Ext.getCmp('queryTextareaId').getValue();
                            btn.up('consolePanel').doAndProcessRequest(serverName, port, query);
                        }
                    }
                ]
            }
        ];
        this.callParent(arguments);
    },
    doAndProcessRequest:function (serverName, port, query) {

        var th = this;
        var sphinxQlMultyResult;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/sphinxQlQueryResult/'+serverName+'/'+port+'',
            method: 'POST',
            useDefaultXhrHeader: false,
            headers: { 'Content-Type': 'application/json' },
            jsonData: {"parameter": query},
            success: function(response) {
                sphinxQlMultyResult = Ext.JSON.decode(response.responseText);
                var status = sphinxQlMultyResult.status;
                if(status && status.code != 0) {
                    th.addErrorPanel(th, status.message);
                } else {
                    th.addResultPanel(th, sphinxQlMultyResult);
                }
            }
        });
    },

    addResultPanel: function(consolePanel, sphinxQlMultyResult){
        var resultPanel = Ext.create('sphinx-console.view.sphinxQLConsole.components.result.ResultPanel',
                            {collectionName: this.collectionName,
                            serverAndPort: Ext.getCmp('serverAndPortComboId').getValue(),
                            queryText: Ext.getCmp('queryTextareaId').getValue(),
                            resultCount: sphinxQlMultyResult.resultList[3].resultList[1][1],
                            queryExecutionTime: sphinxQlMultyResult.resultList[3].resultList[2][1]});
        var resultGrid = resultPanel.down('resultGrid');
        var profileGrid = resultPanel.down('profileGrid');
        var planGrid = resultPanel.down('planGrid');
        var metaGrid = resultPanel.down('metaGrid');

        this.reconfigureGrid(resultGrid, sphinxQlMultyResult.resultList[0]);
        this.reconfigureGrid(profileGrid, sphinxQlMultyResult.resultList[1]);
        this.reconfigureGrid(planGrid, sphinxQlMultyResult.resultList[2]);
        this.reconfigureGrid(metaGrid, sphinxQlMultyResult.resultList[3]);

        this.insertResultPanel(consolePanel, resultPanel);
    },

    insertResultPanel: function (consolePanel, resultPanel){
        consolePanel.insert(1, resultPanel);
    },

    addErrorPanel: function(consolePanel, errorMessage){
        var errorPanel = Ext.create('sphinx-console.view.sphinxQLConsole.components.result.ResultErrorPanel',
                {serverAndPort: Ext.getCmp('serverAndPortComboId').getValue(),
                 queryText: Ext.getCmp('queryTextareaId').getValue(),
                 errorMessage: errorMessage
                });
        this.insertResultPanel(consolePanel, errorPanel);
    },

    reconfigureGrid: function(grid, sphinxQLResult){
        var fields = sphinxQLResult.fields;
        var store = Ext.create('sphinx-console.store.SphinxQLResult', {fields: fields});
        store.add(sphinxQLResult.resultList);

        grid.reconfigure(store, this.buildColumns(fields));
    },

    buildColumns: function(fields){
        var columns = [];
        for (var i = 0; i < fields.length; i++) {
            columns.push(Ext.create('Ext.grid.column.Column', {header: fields[i], dataIndex: fields[i], minWidth: 200, renderer: Ext.String.htmlEncode}));
        }
        return columns;
    }
});