Ext.define('sphinx-console.view.searchQuery.history.Search', {
    extend: 'Ext.form.Panel',
    alias: 'widget.queryHistorySearch',
    id: 'queryHistorySearchPanel',
    collectionName: null,
    serverAndPortValue: null,
    queryText: null,
    fromDate: null,
    fromTime: null,
    toDate: null,
    toTime: null,
    minHeight: 290,
    maxHeight: 290,
    dateDetailingStore: Ext.create('sphinx-console.store.DateDetailing'),
    graphViewTypes: {
        bar: [dygraphsphinx-consolePlotters.barChartPlotter],
        line: null
    },

    initComponent: function(){
        var th = this;
        this.items = [
            {
                xtype : 'fieldset',
                title : 'Параметры поиска',
                width: '100%',
                margin: 20,
                defaults: {
                    labelWidth: 205,
                    margin: 5
                },
                items: [
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        items: [
                            {
                                xtype: 'combo',
                                fieldLabel: 'Реплика',
                                itemId: 'serverAndPortComboId',
                                displayField: 'serverAndPort',
                                store: Ext.create('sphinx-console.store.ReplicasData', {collectionName: th.collectionName}),
                                value: th.serverAndPortValue,
                                emptyText: 'Все',
                                labelWidth: 205,
                                width: 820,
                                editable: false
                            },
                            {
                                flex: 1
                            },
                            {
                                xtype: 'combo',
                                fieldLabel: 'Уровень детализации',
                                itemId: 'detailLevelComboId',
                                displayField: 'title',
                                valueField: 'value',
                                store: th.dateDetailingStore,
                                editable: false,
                                labelWidth: 180,
                                width: 500,
                                margin: '0 5 0 0'
                            },
                        ]
                    },
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        items: [
                            {
                                xtype: 'datefield',
                                itemId: 'fromDate',
                                value: th.fromDate,
                                margin: '0 5 0 0',
                                fieldLabel: 'Дата и время выполнения c:',
                                labelSeparator: '',
                                labelWidth: 205,
                                width: 375,
                                format: 'd.m.Y'
                            },
                            {
                                xtype: 'timefield',
                                itemId: 'fromTime',
                                value: th.fromTime,
                                format: 'H:i',
                                increment: 30,
                                width: 120
                            },
                            {
                                xtype: 'datefield',
                                itemId: 'toDate',
                                value: th.toDate,
                                margin: '0 5 0 10',
                                fieldLabel: 'по',
                                labelWidth: 20,
                                width: 185,
                                labelSeparator: '',
                                format: 'd.m.Y'
                            },
                            {
                                xtype: 'timefield',
                                itemId: 'toTime',
                                value: th.toTime,
                                format: 'H:i',
                                increment: 30,
                                width: 120
                            },
                            {
                                flex: 1
                            },
                            {
                                xtype: 'combo',
                                fieldLabel: 'Вид графика',
                                itemId: 'graphViewType',
                                displayField: 'title',
                                valueField: 'value',
                                store: new Ext.data.SimpleStore({
                                    fields: ['title', 'value'],
                                    data: [['Bar', th.graphViewTypes.bar],['Line', th.graphViewTypes.line]]
                                }),
                                editable: false,
                                labelWidth: 180,
                                width: 500,
                                margin: '0 5 0 0'
                            },
                        ]
                    },
                    {   xtype: 'textarea',
                        height: 100,
                        /*почему то не получается задать отступ от правого края с помощью margin и padding элемента и контейнера, поэтому такой костыль*/
                        width: '99%',
                        autoScroll: true,
                        readOnly: true,
                        fieldLabel: 'Запрос',
                        value: th.queryText
                    },
                    {
                        xtype: 'button',
                        text: 'Построить графики',
                        cls: 'searchbutton',
                        handler: function () {
                            Ext.getCmp('queryHistorySearchPanel').findQueryHistory();
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Очистить фильтр',
                        cls: 'clearfilterbutton',
                        handler: function (searchButton) {
                            searchButton.up('form').getForm().reset();
                        }
                    }
                ]
            }
        ];
        th.dateDetailingStore.load({callback: function (records, operation, success) {
            Ext.getCmp('queryHistorySearchPanel').down('#detailLevelComboId').setValue(records[records.length - 1].data.value);
        }});
        this.callParent(arguments);
    },

    findQueryHistory: function(){
        var searchParameters = new Object();
        searchParameters.collectionName = this.collectionName;
        searchParameters.query = this.queryText;
        var serverAndPort = Ext.getCmp('queryHistorySearchPanel').down('#serverAndPortComboId').getValue();
        if(serverAndPort){
            var serverName = serverAndPort.split(' : ')[0];
            var port = serverAndPort.split(' : ')[1];
            var replicaName  = new Object();
            replicaName.serverName = serverName;
            replicaName.port = port;
            searchParameters.replicaName = replicaName;
        }
        var fromDate = this.getFromDate();
        var toDate = this.getToDate();

        searchParameters.dateFrom = fromDate ? fromDate.getTime() : null;
        searchParameters.dateTo = toDate ? toDate.getTime() : null;
        searchParameters.dateDetailing = Ext.getCmp('queryHistorySearchPanel').down('#detailLevelComboId').getValue();

        this.buildAllGraphs(searchParameters);
    },

    getFromDate: function(){
        var fromDate = Ext.getCmp('queryHistorySearchPanel').down('#fromDate').getValue();
        var fromTime = Ext.getCmp('queryHistorySearchPanel').down('#fromTime').getValue();
        return sphinx-console.util.Date.addTimeToDate(fromDate, fromTime);
    },

    getToDate: function(){
        var result;
        var toDate = Ext.getCmp('queryHistorySearchPanel').down('#toDate').getValue();
        var toTime = Ext.getCmp('queryHistorySearchPanel').down('#toTime').getValue();
        result = sphinx-console.util.Date.getEndOfDayIfTimeEmpty(toDate, toTime);
        return result;
    },

    buildGraph: function (activeTab, data) {

        var th = this;
        var el = document.getElementById(activeTab.getId());

        var data_str = '';
        var minMaxAvg = data[0].values.length > 1;
        for (var i = 0; i < data.length; i++) {
            data_str = data_str + data[i].date + "," + data[i].values + "\n";
        }

        var fromDate = this.getFromDate();
        if(fromDate == null){
            fromDate = data[0].date;
        }
        var toDate = this.getToDate();
        if(toDate == null){
            toDate = data[data.length-1].date;
        }

        var graph_options = new Object();
        graph_options.labels = minMaxAvg ? ["Date", "Min", "Max", "Avg"] : ["Date", "Value"];
        graph_options.dateWindow = [ fromDate, toDate ];
        graph_options.axes = {
            x: {
                axisLabelFormatter: th.dateFormatterLabelForTimestamp,
                valueFormatter: th.dateFormatterValueForTimestamp,
                ticker: Dygraph.dateTicker
            }
        }
        var customPlotter = Ext.getCmp('queryHistorySearchPanel').down('#graphViewType').getValue();
        if(customPlotter){
            graph_options.plotter = [this.getPlotter()];
        }

        activeTab.dygraphObject = new Dygraph(el,
            data_str,
            graph_options
        );
    },

    getPlotter: function(){
        return dygraphsphinx-consolePlotters.barChartPlotter;
    },

    dateFormatterValueForTimestamp: function(timestamp, granularity, opts, dygraph){
        var milliseconds = new Date(timestamp).getMilliseconds();
        return Dygraph.dateString_(timestamp) + (milliseconds ? '.' + (milliseconds > 99 ? milliseconds : milliseconds < 10 ? "00" + milliseconds : "0" + milliseconds) : '');
    },

    dateFormatterLabelForTimestamp: function(timestamp, granularity, opts){
        return Dygraph.dateAxisLabelFormatter(new Date(timestamp), granularity, opts);
    },

    buildAllGraphs: function(searchParameters) {
        var th = this;
        var tabpanel = Ext.getCmp('queryHistorySearchPanel').up('window').down('queryHistoryTabs').down('tabpanel');
        var tabs = tabpanel.items.items;
        tabs.forEach(function (element, index, array) {
            var tab = tabs[index];
            var methodName = tab.methodName;
            var data = th.getDataForGrid(methodName, searchParameters);
            th.buildGraph(tab, data);
        })
    },

    getDataForGrid: function(methodName, searchParameters) {
        var th = this;
        var result;
        Ext.Ajax.request({
            async: false,
            url:  sphinx-console.util.Utilities.SERVER_URL + '/view/' + methodName,
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            params: Ext.JSON.encodeValue(searchParameters, '\n'),
            waitTitle:'Connecting',
            waitMsg:'Collecting...',
            method: 'POST',
            callback: function(options, success, response) {
                if(success){
                    result = Ext.JSON.decode(response.responseText);
                }
                else{
                    sphinx-console.util.ErrorMessage.showFailureResponse(response);
                }
            }
        });
        return result;
    }

});