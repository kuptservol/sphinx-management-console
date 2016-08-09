Ext.define('sphinx-console.view.adminProcess.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.adminProcessList',
    store: 'AdminProcess',
    forceFit: true,          //Fit to container
    columnLines: true,
    viewConfig: {
        autoScroll: true
    },
    requires: 'sphinx-console.model.AdminProcess',
    height: 277,
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
                    width: 63,
                    items : [{
                        icon: 'app/resources/images/edit.png',
                        tooltip : 'Редактировать параметры процесса',
                        handler : function(grid, rowIndex) {
                            sphinx-console.app.fireEvent('editAdminProcessPopup',this.up('panel').getRecordByRowIndex(grid.getStore(),rowIndex));
                        }
                    },{
                        icon: 'app/resources/images/delete.png',
                        tooltip : 'Удалить процесс',
                        handler : function(grid, rowIndex) {
                            Ext.Msg.show({
                                title:'Предупреждение',
                                message: 'Удалить процесс?',
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
                                                sphinx-console.app.fireEvent('removeAdminProcess',record.data.id);
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
                    header: 'ID',
                    dataIndex: 'idAdminProcess',
                    sortable: false,
                    menuDisabled: true
                },
                {
                    header: 'Тип процесса',
                    dataIndex: 'type',
                    sortable: false,
                    menuDisabled: true,
                    renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                        if(record.data.type=='COORDINATOR') return 'Координатор';
                        else if(record.data.type=='SEARCH_AGENT') return 'Агент поиска';
                        else if(record.data.type=='INDEX_AGENT') return 'Агент индексации';
                    }
                },
                {
                    header: 'Порт',
                    dataIndex: 'port',
                    sortable: false,
                    menuDisabled: true
                },
                {
                    header: 'Статус',
                    dataIndex: 'status',
                    sortable: false,
                    menuDisabled: true,
                    width: 63,
                    renderer: function(v, meta, rec){
                        Ext.Ajax.request({
                            url: sphinx-console.util.Utilities.SERVER_URL + '/view/adminProcessStatus',
                            method: 'POST',
                            useDefaultXhrHeader: false,
                            headers: { 'Content-Type': 'application/json' },
                            jsonData: {
                                "id": rec.data.id
                            },
                            success: function(response, opts) {
                                switch(response.responseText){
                                    case "\"RUNNING\"" : Ext.get("status"+opts.jsonData.id).update("Запущен");break;
                                    case "\"STOPPED\"" : Ext.get("status"+opts.jsonData.id).update("Остановлен");break;
                                }

                            }
                        });
                        return '<div id="status'+rec.data.id+'"></div>';
                    }
                }
            ];
        this.dockedItems = [ {
            xtype : 'pagingtoolbar',
            store : 'AdminProcess',
            dock : 'bottom',
            padding: '3 3 14 3',
            plugins: [new sphinx-console.view.PageSizePlugin(function(){
                sphinx-console.app.fireEvent("refreshAdminProcesses");
            })],
            displayMsg  : 'Записи {0} - {1} из {2}',
			refreshText: 'Обновить',
			beforePageText: 'Страница',
			afterPageText: 'из {0}',
			displayInfo : true
        } ];
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

