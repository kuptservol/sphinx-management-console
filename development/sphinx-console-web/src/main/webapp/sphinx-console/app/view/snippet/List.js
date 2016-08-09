Ext.define('sphinx-console.view.snippet.List', {
    extend: 'Ext.grid.Panel',
    id: 'snippetList',
    alias: 'widget.snippetList',
    requires: ['sphinx-console.view.snippet.wizard.SnippetWizardPanel','sphinx-console.view.snippet.wizard.SnippetWizardWindow', 'sphinx-console.util.Utilities'],
    store: 'SnippetData',
    forceFit: true,          //Fit to container
    columnLines: true,
    minHeight: 100,
    maxHeight: 550,
    viewConfig: {
        scroll: false,
        style: {overflow: 'auto', overflowX: 'hidden'}
    },
    autoScroll: false,
    snippetWizard: null,

    columns: [
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
                    handler: function (grid, rowIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        sphinx-console.app.fireEvent('getLogCollection', rec.get('collectionName'));
                    }
                },
                {
                    icon: 'app/resources/images/json.png',
                    tooltip: 'JSON',
                    handler: function (grid, rowIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        sphinx-console.app.fireEvent('getSnippetJson', rec.get('collectionName'));
                    }
                },
                {
                    icon: 'app/resources/images/edit.png',
                    tooltip: 'Редактировать',
                    handler: function (grid, rowIndex, colIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        if(!this.up('grid').wizard) {
                            var window = Ext.create('sphinx-console.view.snippet.wizard.SnippetWizardWindow', {
                                title: 'Редактирование сниппета',
                                items: [
                                    {
                                        xtype: 'snippetWizardPanel',
                                        record: rec
                                    }
                                ]
                            });
                            window.show();
/*                            this.items[2].disable();
                            this.up('grid').down('#addCollection').setDisabled(true);*/
                            this.up('grid').snippetWizard = window;
                        }
                    }
                },
                {
                    icon: 'app/resources/images/delete.png',
                    tooltip: 'Удалить',
                    handler: function (grid, rowIndex, colIndex) {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.getCmp('snippetList').deleteSnippet(rec.get('collectionName'));
                    }
                }
            ]
        },
        {
            header: 'Коллекция',
            dataIndex: 'collectionName',
            flex: 1
        },
        {
            header: 'Построение сниппета',
            xtype: 'templatecolumn',
            maxWidth: 350,
            minWidth: 350,
            tpl: '<tpl if="isCurrentlyRebuildSnippet"><input class="btn-stop" type="button" title="Остановить пересбор сниппета" onclick="onStopRebuild(\'{collectionName}\')">' +
            '<tpl else><input class="btn-start" type="button" title="Запустить пересбор сниппета" onclick="onStartRebuild(\'{collectionName}\')"></tpl>' +

            '<tpl if="isSheduleEnabled"><input class="calendarRunning" style="vertical-align: middle" type="button" title="Расписание запущено">' +
            '<tpl else><input class="calendarStopped" style="vertical-align: middle" type="button" title="Расписание остановлено"></tpl>' +

            '<input class="cron" type="text" value = "{cronSchedule}" pattern="">' +

            '<input class="btn-calendar" type="button" title="Установить расписание" onclick="onSetCronSheduler(\'{collectionName}\',this)">' +

            '<tpl if="isSheduleEnabled"><input class="btn-stop" type="button" title="Остановить расписание пересбора сниппета" onclick="onDisableShedule(\'{collectionName}\')">' +
            '<tpl else><input class="btn-start" type="button" title="Запустить расписание пересбора сниппета" onclick="onEnableShedule(\'{collectionName}\')"></tpl>' +

            '<tpl if="isCurrentlyFullRebuildSnippet"><input class="btn-stop" type="button" title="Остановить полный пересбор сниппета" onclick="onStopFullRebuild(\'{collectionName}\')">' +
            '<tpl else><input class="btn-full-rebuild" type="button" title="Запустить полный пересбор сниппета" onclick="onStartFullRebuild(\'{collectionName}\')"></tpl>'

        },
        {
            header: 'Предыдущее построение',
            xtype: 'templatecolumn',
            align: 'center',
            maxWidth: 250,
            minWidth: 250,
            tpl: '<span>{lastBuildSnippetString}</span>' +
            '<tpl if="lastBuildSnippetString"><input class="btn-log" style="margin-left:10px" type="button" title="Просмотр лога последнего пересбора" onclick="onShowLastSnippetLog(\'{collectionName}\')"></tpl>'
        },
        {
            header: 'Следующее построение',
            align: 'center',
            dataIndex: 'nextBuildSnippet',
            maxWidth: 250,
            minWidth: 250,
            xtype: 'datecolumn',
            renderer: Ext.util.Format.dateRenderer('d.m.Y H:i:s')
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            items: [{
                itemId: 'refreshSnippets',
                xtype: 'button',
                text: 'Обновить',
                handler: function () {
                    Ext.getCmp('snippetSearchPanel').findSnippets();
                }
            },
                '-',
                {
                    itemId: 'addSnippet',
                    text: 'Добавить сниппет',
                    handler: function () {
                        if(!this.up('grid').snippetWizard) {
                            var window = Ext.create('sphinx-console.view.snippet.wizard.SnippetWizardWindow', {
                                title: 'Создание сниппетов для коллекции',
                                items: [{
                                    xtype: 'snippetWizardPanel'
                                }]
                            });
                            window.show();
                            var grid = this.up('grid');
                            this.up('grid').snippetWizard = window;
                        }
                    }
                }

            ]
        },
        {
            id: 'snippetPagingToolBar',
            xtype: 'pagingtoolbar',
            store: 'SnippetData',
            dock: 'bottom',
            plugins: [new sphinx-console.view.PageSizePlugin()],
            displayMsg: 'Записи {0} - {1} из {2}',
            beforePageText: 'Страница',
            afterPageText: 'из {0}',
            refreshText: 'Обновить',
            displayInfo: true
        }
    ],

    rebuildSnippet: function (collectionName) {
        var th = this;

        Ext.MessageBox.buttonText.yes = "Да";
        Ext.MessageBox.buttonText.no = "Нет";
        Ext.MessageBox.confirm('Сниппеты', 'Запустить пересбор сниппета для коллекции ' + collectionName + '?', function (confirmBtn) {
            if (confirmBtn === 'yes') {
                Ext.Ajax.request({
                    url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/rebuildSnippets/' + collectionName,
                    method: 'GET',
                    success: function (response) {
                        var status = Ext.JSON.decode(response.responseText);
                        if (status && status.code != 0) {
                            sphinx-console.util.ErrorMessage.showFailureResponseWindow(status);
                        }
                    }
                });
                Ext.getCmp('snippetSearchPanel').findSnippets();
            }
        });
    },

    stopRebuildSnippet: function (collectionName) {
        Ext.MessageBox.buttonText.yes = "Да";
        Ext.MessageBox.buttonText.no = "Нет";
        Ext.MessageBox.confirm('Сниппеты', 'Остановить пересбор сниппета для коллекции ' + collectionName + '?', function (confirmBtn) {
            if (confirmBtn === 'yes') {
                Ext.Ajax.request({
                    url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/stopRebuildSnippets/' + collectionName,
                    method: 'GET',
                    success: function (response) {
                        var status = Ext.JSON.decode(response.responseText);
                        if (status && status.code != 0) {
                            sphinx-console.util.ErrorMessage.showFailureResponseWindow(status);
                        }
                    }
                });
                Ext.getCmp('snippetSearchPanel').findSnippets();
            }
        });
    },

    deleteSnippet: function (collectionName) {
        var th = this;

        Ext.MessageBox.buttonText.yes = "Да";
        Ext.MessageBox.buttonText.no = "Нет";
        Ext.MessageBox.confirm('Сниппеты', 'Удалить сниппет коллекции ' + collectionName + '?', function (confirmBtn) {
            if (confirmBtn === 'yes') {
                Ext.Ajax.request({
                    url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/deleteSnippetConfiguration/' + collectionName,
                    method: 'GET',
                    success: function (response) {
                        var status = Ext.JSON.decode(response.responseText);
                        if (status && status.code != 0) {
                            sphinx-console.util.ErrorMessage.showFailureResponseWindow(status);
                        }
                    }
                });
                Ext.getCmp('snippetSearchPanel').findSnippets();
            }
        });
    },

    enableShedule: function (collectionName) {
        Ext.MessageBox.buttonText.yes = "Да";
        Ext.MessageBox.buttonText.no = "Нет";
        Ext.MessageBox.confirm('Сниппеты', 'Запустить расписание пересбора сниппета для коллекции ' + collectionName + '?', function (confirmBtn) {
            if (confirmBtn === 'yes') {
                Ext.Ajax.request({
                    url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/enableScheduling/' + collectionName + '/BUILD_SNIPPET',
                    method: 'POST',
                    success: function (response) {
                        var status = Ext.JSON.decode(response.responseText);
                        if (status && status.code != 0) {
                            sphinx-console.util.ErrorMessage.showFailureResponseWindow(status);
                        }
                    }
                });
            }
        });
    },

    disableShedule: function (collectionName) {
        Ext.MessageBox.buttonText.yes = "Да";
        Ext.MessageBox.buttonText.no = "Нет";
        Ext.MessageBox.confirm('Сниппеты', 'Остановить расписание пересбора сниппета для коллекции ' + collectionName + '?', function (confirmBtn) {
            if (confirmBtn === 'yes') {
                Ext.Ajax.request({
                    url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/disableScheduling/' + collectionName + '/BUILD_SNIPPET',
                    method: 'POST',
                    success: function (response) {
                        var status = Ext.JSON.decode(response.responseText);
                        if (status && status.code != 0) {
                            sphinx-console.util.ErrorMessage.showFailureResponseWindow(status);
                        }
                    }
                });
            }
        });
    },

    startFullRebuild: function(collectionName) {
        Ext.MessageBox.buttonText.yes = "Да";
        Ext.MessageBox.buttonText.no = "Нет";
        Ext.MessageBox.confirm('Сниппеты', 'Запустить полный пересбор сниппета для коллекции ' + collectionName + '?', function (confirmBtn) {
            if (confirmBtn === 'yes') {
                Ext.Ajax.request({
                    url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/makeSnippetsFullRebuild/' + collectionName,
                    method: 'GET',
                    success: function (response) {
                        var status = Ext.JSON.decode(response.responseText);
                        if (status && status.code != 0) {
                            sphinx-console.util.ErrorMessage.showFailureResponseWindow(status);
                        }
                    }
                });
            }
        });
    },

    stopFullRebuild: function(collectionName) {
        Ext.MessageBox.buttonText.yes = "Да";
        Ext.MessageBox.buttonText.no = "Нет";
        Ext.MessageBox.confirm('Сниппеты', 'Остановить полный пересбор сниппета для коллекции ' + collectionName + '?', function (confirmBtn) {
            if (confirmBtn === 'yes') {
                Ext.Ajax.request({
                    url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/stopFullRebuildSnippets/' + collectionName,
                    method: 'GET',
                    success: function (response) {
                        var status = Ext.JSON.decode(response.responseText);
                        if (status && status.code != 0) {
                            sphinx-console.util.ErrorMessage.showFailureResponseWindow(status);
                        }
                    }
                });
            }
        });
    },

    setCronShedule: function(collectionName, cronExpression) {
        Ext.MessageBox.buttonText.yes = "Да";
        Ext.MessageBox.buttonText.no = "Нет";
        Ext.MessageBox.confirm('Сниппеты', 'Установить новое расписание \"' + cronExpression + '\" пересбора сниппетов для коллекции ' + collectionName + '?', function (confirmBtn) {
            if (confirmBtn === 'yes') {
                var updateSheduleWrapper = new Object();
                updateSheduleWrapper.collectionName = collectionName;
                updateSheduleWrapper.cronExpression = cronExpression;
                updateSheduleWrapper.type = 'BUILD_SNIPPET';

                Ext.Ajax.request({
                    async: false,
                    url:  sphinx-console.util.Utilities.SERVER_URL + '/configuration/changeCollectionUpdateSchedule',
                    headers: {
                        'Content-Type': 'application/json;charset=utf-8'
                    },
                    params: Ext.JSON.encodeValue(updateSheduleWrapper, '\n'),
                    waitTitle:'Connecting',
                    waitMsg:'Creating...',
                    method: 'POST',
                    success: function(response) {
                        var status = Ext.JSON.decode(response.responseText);
                        if (status && status.code != 0) {
                            sphinx-console.util.ErrorMessage.showFailureResponseWindow(status);
                            Ext.getCmp('snippetSearchPanel').findSnippets();
                        }
                    }
                });
            }
        });
    },

    showLastSnippetLog: function (collectionName) {
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/lastSnippetLogTaskUid/' + collectionName,
            method: 'GET',
            async: false,
            success: function (response) {
                var resultWrapper = Ext.JSON.decode(response.responseText);
                var taskUid = resultWrapper ? resultWrapper.result : null;
                if (taskUid) {
                    Ext.create('sphinx-console.view.logs.LogWindow', {uid: taskUid}).show();
                }
            }
        });
    }
});

function onStartRebuild(collectionName) {
    Ext.getCmp('snippetList').rebuildSnippet(collectionName);
}

function onStopRebuild(collectionName) {
    Ext.getCmp('snippetList').stopRebuildSnippet(collectionName);
}

function onSetCronSheduler(collectionName, th) {
    var cronExpression = th.previousSibling.value;
    Ext.getCmp('snippetList').setCronShedule(collectionName, cronExpression);
}

function onStopFullRebuild(collectionName) {
    Ext.getCmp('snippetList').stopFullRebuild(collectionName);
}

function onStartFullRebuild(collectionName) {
    Ext.getCmp('snippetList').startFullRebuild(collectionName);
}

function onEnableShedule(collectionName) {
    Ext.getCmp('snippetList').enableShedule(collectionName);
}

function onDisableShedule(collectionName) {
    Ext.getCmp('snippetList').disableShedule(collectionName);
}

function onShowLastSnippetLog(collectionName) {
    Ext.getCmp('snippetList').showLastSnippetLog(collectionName);
}
