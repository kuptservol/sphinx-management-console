Ext.define('sphinx-console.view.servers.List', {
    id: 'serversView',
    extend: 'Ext.grid.Panel',
    alias: 'widget.serversList',
    store: 'Servers',
    forceFit: true,          //Fit to container
    columnLines: true,
    minHeight: 100,
    maxHeight: 550,
    viewConfig : {
        scroll:false,
        style:{overflow: 'auto',overflowX: 'hidden'}
    },
    autoScroll: false,
    requires: ['sphinx-console.model.Server'],
    autoResizeColumns: true,
    initComponent: function () {
        this.columns =
            [
                {
                    header: 'Действия',
                    dataIndex: 'id',
                    sortable: false,
                    menuDisabled: true,
                    xtype : 'actioncolumn',
                    width: 25,
                    items : [{
                        icon: 'app/resources/images/process.png',
                        tooltip : 'Просмотреть процессы на сервере',
                        handler : function(grid, rowIndex) {
                            sphinx-console.app.fireEvent('showAdminProcessPopupList',this.up('panel[id=serversView]').getRecordByRowIndex(grid.getStore(),rowIndex));
                        }
                    },{
                        icon: 'app/resources/images/json.png',
                        tooltip: 'JSON',
                        handler: function (grid, rowIndex) {
                            var serversView = this.up('panel[id=serversView]');
                            var rec = serversView.getRecordByRowIndex(grid.getStore(),rowIndex);
                            sphinx-console.app.fireEvent('getServerJson', rec.get('name'));
                        }
                    },{
                        icon: 'app/resources/images/edit.png',
                        tooltip : 'Редактировать параметры сервера',
                        handler : function(grid, rowIndex) {
                            var serversView = this.up('panel[id=serversView]');
                            Ext.create('sphinx-console.view.servers.EditServerWindow').setRecord(serversView.getRecordByRowIndex(grid.getStore(),rowIndex)).show();
                            //serversView.serverPopup.setRecord(serversView.getRecordByRowIndex(grid.getStore(),rowIndex)).show();
                        }
                    },{
                        icon: 'app/resources/images/delete.png',
                        tooltip : 'Удалить сервер',
                        handler : function(grid, rowIndex, cellIndex, item, event, record) {
                            Ext.Msg.show({
                                title:'Предупреждение',
                                message: 'Внимание, удаление сервера не будет произведено, если на сервере работают sphinx процессы для коллекций! Удалить сервер?',
                                buttonText: {
                                    yes: 'Да',
                                    no: 'Нет'
                                },
                                buttons: Ext.Msg.YESNO,
                                icon: Ext.Msg.QUESTION,
                                fn: function(btn) {
                                    if (btn === 'yes') {
                                        grid.getStore().each(function(record,idx){
                                            if(idx==rowIndex){
                                                grid.getStore().remove(record);
                                                grid.getStore().sync({
                                                    scope: this,
                                                    success: function (batch,options) {
                                                        sphinx-console.app.fireEvent('refreshServersOnTab');
                                                        sphinx-console.app.fireEvent('afterDeleteServer');
                                                    }
                                                });
                                                return;
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }]
                },
                {
                    header: 'Адрес',
                    dataIndex: 'ip',
                    sortable: false,
                    menuDisabled: true
                },
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
                }
            ];
        this.dockedItems = [{
            xtype: 'toolbar',
            items: [{
                xtype: 'button',
                text: 'Обновить',
                handler: function(){
                    sphinx-console.app.getsphinx-consoleControllerServersController().onRefreshServers();
                }
            },
            {
                xtype: 'button',
                text: 'Добавить сервер',
                handler: function(){
                    Ext.create('sphinx-console.view.servers.AddServerWindow').show();
                }
            }]
        },{
            id: 'serversPagingToolBar',
            xtype : 'pagingtoolbar',
            store : 'Servers',
            dock : 'bottom',
            plugins: [new Ext.ux.ServerListPageSizePlugin()],
            displayMsg  : 'Записи {0} - {1} из {2}',
			refreshText: 'Обновить',
			beforePageText: 'Страница',
			afterPageText: 'из {0}',
			displayInfo : true,
            moveFirst : function(){
                sphinx-console.app.getsphinx-consoleControllerServersController().loadPage(1,Ext.getCmp('serverSearchField').getValue());
            },
            movePrevious : function(){
                sphinx-console.app.getsphinx-consoleControllerServersController().previousPage(Ext.getCmp('serverSearchField').getValue());
            },
            moveNext : function(){
                sphinx-console.app.getsphinx-consoleControllerServersController().nextPage(Ext.getCmp('serverSearchField').getValue());
            },
            moveLast : function(){
                sphinx-console.app.getsphinx-consoleControllerServersController().loadPage(this.getPageData().pageCount,Ext.getCmp('serverSearchField').getValue());
            }
        } ]
        this.callParent(arguments);
    },
    getRecordByRowIndex: function(store,rowIndex){
        var result = null;
        store.each(function(record,idx){
            if(idx==rowIndex) {
                result=record;
                return;
            }
        });
        return result;
    }
});

Ext.ux.ServerListPageSizePlugin = function() {
    Ext.ux.PageSizePlugin.superclass.constructor.call(this, {
        store: new Ext.data.SimpleStore({
            fields: ['text', 'value'],
            data: [['10', 10], ['20', 20],['30', 30]]
        }),
        pluginId: 'serverListPageSizePluginId',
        mode: 'local',
        displayField: 'text',
        valueField: 'value',
        editable: false,
        allowBlank: false,
        triggerAction: 'all',
        width: 60,
        resetToDefault: function(){
            return 'empty';
        }
    });
};


Ext.extend(Ext.ux.ServerListPageSizePlugin, Ext.form.ComboBox, {
    init: function(paging) {
        paging.on('render', this.onInitView, this);
    },
    onInitView: function(paging) {
        paging.add('-',
            this,
            'Количество строк таблицы'
        );
        this.setValue(this.getStore().getAt(0).getData().value);
        this.on('select', this.onPageSizeChanged, paging);
    },
    listeners:{
        afterrender:function(rec){
            this.getStore().setPageSize(rec.value);
        }
    },
    onPageSizeChanged: function(combo) {
        this.getStore().setPageSize(combo.getValue());
        this.pageSize = this.getStore().getPageSize();
        sphinx-console.app.getsphinx-consoleControllerServersController().loadPage(1,Ext.getCmp('serverSearchField').getValue());
    }
});



