Ext.define('sphinx-console.view.collection.List', {
        extend: 'Ext.grid.Panel',
        itemId: 'collectionList',
        alias: 'widget.collectionList',
        store: 'CollectionData',
        forceFit: true,          //Fit to container
        columnLines: true,
        minHeight: 100,
        maxHeight: 550,
        viewConfig : {
            scroll:false,
            style:{overflow: 'auto',overflowX: 'hidden'}
        },
        autoScroll: false,
        requires: ['sphinx-console.model.Collection','sphinx-console.view.collection.wizard.WizardPanel','sphinx-console.view.collection.wizard.WizardWindow',
                    'sphinx-console.view.servers.ServerViewForm','sphinx-console.view.collection.details.search.Window','sphinx-console.view.collection.details.indexing.Window'],
        autoResizeColumns: true,
        wizard: null,

        indexServerHandler: function(collectionName, serverStatus) {
            var action = serverStatus ? 'stopIndexing' : 'startIndexing';
            sphinx-console.app.fireEvent(action, collectionName);
        },

        searchServerHandler: function(collectionName, serverStatus) {
            var action = serverStatus ? 'stopProcess' : 'startProcess';
            sphinx-console.app.fireEvent(action, collectionName);
        },

        getColumnWidget: function(columnName, internalId) {
            return this.getView().getHeaderCt().down('#' + columnName).liveWidgets[internalId];
        },

        initComponent: function () {
            sphinx-console.app.on('wizardclosed', function() {
                if(this.getStore().getRange().length > 0) {
                    this.down('actioncolumn').items[2].enable()
                }
                this.down('#addCollection').setDisabled(false);
                this.wizard = null;
            }, this);
            this.columns =
                [
                    {
                    xtype: 'actioncolumn',
                    header: 'Действия',
                    maxWidth: 156,
                    minWidth: 156,
                    align: 'center',
                    items: [
                            {
                                icon: 'app/resources/images/log.png',
                                tooltip: 'Лог',
                                handler: function(grid, rowIndex) {
                                    var rec = grid.getStore().getAt(rowIndex);
                                    sphinx-console.app.fireEvent('getLogCollection', rec.get('name'));
                                }
                            },
                            {
                                icon: 'app/resources/images/process.png',
                                tooltip: 'Конфигурация',
                                handler: function (grid, rowIndex) {
                                    var rec = grid.getStore().getAt(rowIndex);
                                    sphinx-console.app.fireEvent('getConfigurationCollection', rec.get('name'), 1);
                                }
                            },
                            {
                                icon: 'app/resources/images/json.png',
                                tooltip: 'JSON',
                                handler: function (grid, rowIndex) {
                                    var rec = grid.getStore().getAt(rowIndex);
                                    sphinx-console.app.fireEvent('getCollectionJson', rec.get('name'));
                                }
                            },
                            {
                                icon: 'app/resources/images/console.png',
                                tooltip: 'SphinxQL консоль',
                                handler: function (grid, rowIndex) {
                                    var rec = grid.getStore().getAt(rowIndex);
                                    sphinx-console.app.fireEvent('openConsoleWindow', rec.get('name'));
                                }
                            },
                            {
                                icon: 'app/resources/images/edit.png',
                                tooltip: 'Редактировать',
                                handler: function (grid, rowIndex, colIndex) {
                                    var rec = grid.getStore().getAt(rowIndex);
                                    if(!this.up('grid').wizard) {
                                        var window = Ext.create('sphinx-console.view.collection.wizard.WizardWindow', {
                                            title: 'Редактирование коллекции',
                                            items: [
                                                {
                                                    xtype: 'collectionWizard',
                                                    record: rec
                                                }
                                            ]
                                        });
                                        window.show();
                                        this.items[2].disable();
                                        this.up('grid').down('#addCollection').setDisabled(true);
                                        this.up('grid').wizard = window;
                                    }
                                }
                            },
                            {
                                icon: 'app/resources/images/delete.png',
                                tooltip: 'Удалить',
                                handler: function (grid, rowIndex, colIndex) {
                                    Ext.MessageBox.buttonText.yes = "Да";
                                    Ext.MessageBox.buttonText.no = "Нет";
                                    Ext.MessageBox.confirm('Удаление коллекции', 'Удалить коллекцию?', function(btn){
                                        if(btn === 'yes'){
                                            var rec = grid.getStore().getAt(rowIndex);
                                            sphinx-console.app.fireEvent('deleteCollection', rec.get('name'));
                                        }
                                    });
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'hiddenfield',
                        id: 'collectionTypeField',
                        dataIndex: 'collectionType'
                    },
                    {
                        header: 'Название',
                        xtype: 'templatecolumn',
                        tpl: '<tpl if="collectionType ==\'SIMPLE\'"><div>{name}<tpl else><span class="distributed-name">{name}</span></tpl>' +
                        '<tpl if="collection.needReload">' +
                            '<input class = "state-note-picture" type="button" title="Необходима перезагрузка распределенной коллекции">' +
                            '<input class="btn-full-rebuild" type="button" title="Запустить перезагрузку распределенной коллекции" onclick="onDistributedCollectionReload(\'{name}\')">' +
                        '</tpl>' +
                        '</div>'
/*                        tpl: '<div>{name}</div><tpl if="collection.needReload"><img src="app/resources/images/theme2/warning.png" title="Необходима перезагрузка распределенной коллекции">' +
                        '<img src="app/resources/images/refresh.png" title="Запустить перезагрузку распределенной коллекции">'*/
                    },
                    {
                        header: 'Поиск',
                        
                            columns:   [
                                //{
                                //    header: 'Состояние',
                                //    xtype: 'actioncolumn',
                                //    maxWidth: 160,
                                //    minWidth: 160,
                                //    align: 'center',
                                //    items: [
                                //            {
                                //                getClass: function (value, meta, record) {
                                //                    return record.searchLoading ? 'tableImage flagLoading' : record.get('searchStatus') ? 'flagRunning' : 'tableImage flagStopped';
                                //                }
                                //            }
                                //            ]
                                //},
                                {
                                            header: 'Ноды',
                                            xtype: 'templatecolumn',
                                            align: 'center',
                                            tpl: '<input type="button" value="Детально" onclick="onSearchDetails(\'{name}\', \'{collectionType}\')">'
                                        },
                                        {
                                            header: 'Управление',
                                            xtype: 'actioncolumn',
                                            width: 130,
                                            align: 'center',
                                            items: [
                                                {
                                                    getClass: function (value, meta, record) {
                                                        var toolTipText = record.get('searchStatus') ? 'Остановить' : 'Запустить';
                                                        meta.tdAttr = 'data-qtip="' + toolTipText + '"';
                                                        return record.searchLoading ? 'tableImage flagLoading' : record.get('searchStatus') ? 'tableImage stopbutton' : 'tableImage startbutton';
                                                    },
                                                    handler: function (grid, rowIndex) {
                                                        var rec = grid.getStore().getAt(rowIndex);
                                                        if(!rec.searchLoading) {
                                                            grid.up('collectionList').searchServerHandler(rec.get('name'), rec.get('searchStatus'));
                                                        }
                                                        rec.searchLoading = true; rec.commit();
                                                    }
                                                }
                                            ]
                                        }
                            ]
                    },
                    {
                        itemId: 'collectionSize',
                        header: 'Кол-во документов в индексе',
                        maxWidth: 120,
                        minWidth: 120,
                        cls: 'text-wrap',
                        dataIndex: 'collectionSize',
                        renderer: Ext.util.Format.numberRenderer('0,000')
                    },
                    {
                        header: 'Индексация',
                        
                        columns:   [
                            {
                                header: 'Состояние',
                                //xtype: 'actioncolumn',
                                xtype: 'templatecolumn',
                                maxWidth: 220,
                                minWidth: 220,
                                align: 'center',
                                tpl:'<tpl if="collectionType == \'DISTRIBUTED\'"><div>Распределенная</div>' +
                                '<tpl elseif="isIndexing"><div>Идёт индексация</div>' +
                                '<tpl elseif="cronSchedule.enabled"><div>По расписанию</div><tpl else>Расписание остановлено</tpl>'
                                //items: [
                                //            {
                                //                //getClass: function (value, meta, record) {
                                //                //    var isSchedulingEnabled = record.get('cronSchedule') ? record.get('cronSchedule').enabled : false;
                                //                //    var toolTipText = record.get('isIndexing') ? 'Идёт индексация' : isSchedulingEnabled ? 'Расписание запущено' : 'Расписание остановлено';
                                //                //    meta.tdAttr = 'data-qtip="' + toolTipText + '"';
                                //                //    return record.get('isIndexing') ? 'tableImage flagLoading' : isSchedulingEnabled ? 'tableImage calendarRunning' : 'tableImage calendarStopped';
                                //                //}
                                //            }
                                //        ]
                                    },{
                                        header: 'Сервер',
                                        width: 180,
                                        xtype: 'templatecolumn',
                                        align: 'center', //{record.collection.isProcessingFailed}/
                                        tpl:  '<tpl if="collectionType == \'DISTRIBUTED\'"><div></div>' +
                                                '<tpl elseif="collection.processingFailed"><img src="app/resources/images/theme2/error.png" title="Коллекция создана с ошибками, измените ее конфигурацию">' +
                                                '<tpl else><input class="btn-server-name" type="button" value="{indexServer.name}" onclick="onIndexingDetails(\'{name}\')" title="{indexServer.name}"></tpl>'
                                    },
                                    {
                                        header: 'Последняя',
                                        dataIndex: 'lastIndexingTime',
                                        maxWidth: 200,
                                        minWidth: 200,
                                        renderer: Ext.util.Format.dateRenderer('d.m.Y H:i:s')
                                    },
                                    {
                                        header: 'Следующая',
                                        dataIndex: 'nextIndexingTime',
                                        maxWidth: 200,
                                        minWidth: 200,
                                        renderer: Ext.util.Format.dateRenderer('d.m.Y H:i:s')
                                    }
                                    ]
                    }
                ];
                this.dockedItems = [{
                    xtype: 'toolbar',
                    items: [{
                        itemId: 'refreshCollections',
                        xtype: 'button',
                        text: 'Обновить'
                    }, '-',{
                        itemId: 'addCollection',
                        text: 'Добавить коллекцию',
                        handler : function() {
                            if(!this.up('grid').wizard) {
                                var window = Ext.create('sphinx-console.view.collection.wizard.WizardWindow', {
                                    title: 'Создание коллекции',
                                    items: [{
                                        xtype: 'collectionWizard'
                                    }]
                                });
                                window.show();
                                this.setDisabled(true);
                                var grid = this.up('grid');
                                if(grid.getStore().getRange().length > 0 ) {
                                    grid.down('actioncolumn').items[2].disable();
                                }
                                this.up('grid').wizard = window;
                            }
                        }
                    }
                       
                    ]
                },{
                    id: 'collectionsPagingToolBar',
                    xtype: 'pagingtoolbar',
                    store: 'CollectionData',
                    dock: 'bottom',
                    plugins: [new sphinx-console.view.PageSizePlugin()],
                    displayMsg  : 'Записи {0} - {1} из {2}',
                    beforePageText: 'Страница',
                    afterPageText: 'из {0}',
                    refreshText: 'Обновить',
                    displayInfo: true
                }];
            var th = this;
            Ext.getStore('CollectionData').on("load", function(store, records, successful, eOpts) { //to init widgets
                Ext.getStore('CollectionData').getData().getRange().forEach(function(record){
//                    var widget = th.getColumnWidget('cronSchedule', record.internalId);
//                    widget.down('#cronExpression').setValue(record.get('cronSchedule') ? record.get('cronSchedule').cronSchedule : '');
//                    var isSchedulingEnabled = record.get('cronSchedule') ? record.get('cronSchedule').enabled : false;
//                    var calendarButton = widget.down('#applyCalendar');
//                    calendarButton.setTooltip(isSchedulingEnabled ? 'Остановить расписание': 'Запустить расписание');
//                    calendarButton.addCls(isSchedulingEnabled ? 'stopbutton' : 'applyCalendarButton');
                });
            });
            this.callParent(arguments);
        }
});

function onServerClick(name, ip) {
    Ext.create('sphinx-console.view.servers.ServerViewForm',{name: name, ip: ip}).show();
}

function onSearchDetails(collectionName, collectionType) {
	Ext.getStore('ReplicasData').data.clear();
	Ext.create('sphinx-console.view.collection.details.search.Window', collectionName, collectionType).show();
}

function onIndexingDetails(collectionName) {
    Ext.create('sphinx-console.view.collection.details.indexing.Window', collectionName).show();
}

function onDistributedCollectionReload(collectionName) {
    sphinx-console.app.fireEvent('startDistributedCollectionReload', collectionName);
}