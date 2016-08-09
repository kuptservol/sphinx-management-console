Ext.define('sphinx-console.view.collection.wizard.StepDistributed5', {
    extend: 'Ext.form.Panel',
    alias: 'widget.stepDistributed5',
    title: 'Выбор сервера',
    trackResetOnLoad: true,
    serversStore: null,
    selectedSearchServerName: null,
    serversTaskRunner: Ext.create('sphinx-console.util.ServersTaskRunner'),

    listeners: {
        activate: function(th) {
            this.serversStore.load({
                callback: function(records){
                    this.serversTaskRunner.startUpdateInfo(records);
                    this.selectedSearchServerName = this.selectedSearchServerName ? this.selectedSearchServerName : null;
                },
                scope: this
            });
            this.down('#isServersSelected').setValue(th.isServersSelected());
        },
        deactivate: function() {
            this.serversTaskRunner.destroy();
        },
        validitychange: function(form, valid, eOpts){
            this.up('wizard').fireEvent('activeFormValidityChange');
        }
    },

    getData : function(){
        return null;
    },

    loadData: function(searchServer) {
        this.selectedSearchServerName = searchServer.name;
    },

    initComponent: function() {
        this.callParent(arguments);
        var grid = this.down('gridpanel');
        this.serversStore = grid.store;
        sphinx-console.app.on('afterUpdateServer', this.onAfterUpdateServer, this);
        sphinx-console.app.on('afterDeleteServer', this.onAfterDeleteServer,this);
        sphinx-console.app.on('successAddAdminProcess', this.onSuccessAddAdminProcess, this);
        sphinx-console.app.on('successAddAdminProcesses', this.onSuccessAddAdminProcess, this);
    },

    //TODO need put into controller ...
    getSearchServerData : function(){
        return this.down('#serversGrid').store.findRecord('name', this.selectedSearchServerName, false, false).getData(true);
    },

    onAfterUpdateServer: function() {
        this.serversTaskRunner.destroy();
        this.serversTaskRunner = new sphinx-console.util.ServersTaskRunner();
        this.serversStore.load({
            callback: function(records){
                this.serversTaskRunner.startUpdateInfo(records);
            },
            scope: this
        });
    },

    onAfterDeleteServer: function() {
        this.serversTaskRunner.destroy();
        this.serversTaskRunner = new sphinx-console.util.ServersTaskRunner();
        this.serversStore.load({
            callback: function(records){
                var th = this;
                this.serversTaskRunner.startUpdateInfo(records);
                var searchServerSelected = false;
                records.forEach(function(record) {
                    if(record.get('name') == th.selectedSearchServerName) {
                        searchServerSelected = true;
                    }
                });
            },
            scope: this
        });
    },

    onSuccessAddAdminProcess: function(){
        this.down('#serversGrid').getView().refresh();
    },

    isServersSelected: function() {
        return this.selectedSearchServerName != null ? true : '';
    },

    items: [
        {
            xtype: 'textfield', hidden: 'true', itemId: 'isServersSelected', value: null, allowBlank: false
        },
        {
            xtype: 'label',
            padding: '20 20 20 20',
            text: 'Выберите сервер на котором будет располагаться коллекция'
        },
        {
            xtype: 'gridpanel',
            itemId: 'serversGrid',
            autoScroll: true,
            height: 250,
            forceFit: true,
            padding: '20 20 0 20',
            columnLines: true,
            store: Ext.create('sphinx-console.store.Servers', {pageSize: 10}),
            getAdminProcessesCount: function(serverId, type) {
                var result = null;
                Ext.Ajax.request({
                    async: false,
                    url: sphinx-console.util.Utilities.SERVER_URL + '/view/adminProcesses/server/' + serverId,
                    useDefaultXhrHeader: false,
                    headers: { 'Content-Type': 'application/json' },
                    method: 'GET',
                    success: function (response) {
                        result = Ext.JSON.decode(response.responseText);
                    }
                });
                var count = 0;
                if(result) {
                    result.forEach(function (process) {
                        if (process.type == type) {
                            count++;
                        }
                    });
                }
                return result ? count : 0;
            },
            columns: [
                {
                    header: 'Сервер',
                    dataIndex: 'name',
                    sortable: false,
                    menuDisabled: true
                },
                {
                    header: 'Статус',
                    dataIndex: 'status',
                    sortable: false,
                    menuDisabled: true
                },
                {
                    header: 'Сервер поиска',
                    align: 'center',
                    sortable: false,
                    menuDisabled: true,
                    renderer: function (value, metaData, record, rowIdx, colIdx, store) {
                        var checked = this.up('stepDistributed5').selectedSearchServerName == record.get('name') ? 'checked=checked' : '';
                        var disabled = 'disabled=true';
                        if(!this.up('wizard').isEdit) {
                            var adminProcessesCount = this.getAdminProcessesCount(record.get('id'), 'SEARCH_AGENT');
                            disabled = adminProcessesCount > 0 ? '' : 'disabled=true';
                        }
                        return '<input type="radio" name="selectedSearchServerDistributed" ' + checked + ' ' + disabled + ' onclick="onSearchServerDistributedClick(\''+ record.get('name') +'\')" />';
                    }
                }
            ]
        }
    ]
});

function onSearchServerDistributedClick(name) {
    var th = Ext.ComponentQuery.query('stepDistributed5')[0]
    th.selectedSearchServerName = name;
    th.down('#isServersSelected').setValue(th.isServersSelected());
    th.fireEvent('validitychange');
}