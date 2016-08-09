Ext.define('sphinx-console.view.collection.details.indexing.Window', {
    extend: 'Ext.window.Window',
    width: 900,
    //height: 660,
    resizable: true,
    maximizable: false,
    closable: true,
    modal: true,
    layout: 'fit',
    collectionWrapper: null,
    collectionInfo: null,
    server: null,
    defaults: {
        margin: 10,
        bodyPadding: '10%'
    },

    fullIndexingStates: {
        NOT_RUNNING : 'app/resources/images/flag_calendar_stopped.png',
        RUNNING : 'app/resources/images/loading.gif',
        STOP : 'app/resources/images/theme2/stop.png',
        ERROR : 'app/resources/images/bug_error.png',
        IN_PROCESS : 'app/resources/images/theme2/flag_executing.png',
        READY_FOR_APPLY : 'app/resources/images/refresh.png',
        ERROR_APPLY : 'app/resources/images/bug_error.png',//ERROR_APPLY : 'app/resources/images/theme2/error.gif',
        OK : 'app/resources/images/accept.png'
    },

    constructor: function(collectionName) {
        this.title = 'Индексация по коллекции ' + collectionName;
        this.collectionWrapper = this.getCollectionWrapper(collectionName);
        this.superclass.constructor.call(this);
    },

    refresh: function(collectionName) {
        this.collectionInfo = this.getCollectionInfo(collectionName);
        this.refreshUI(this.collectionInfo);
    },

    onNotRunningState: function(collectionInfo) {
        // INDEXING
        this.down('#serverFormField').setDisabled(false);

        this.down('#indexingStatus').setVisible(false);
        this.down('#indexingStatus').setText('');

        this.down('#indexingLogButton').setVisible(false);

        this.down('#indexingBugButton').setVisible(false);

        this.down('#startFullIndexingButton').setVisible(true);
        this.down('#stopFullIndexingButton').setVisible(false);

        //UPDATE
        this.down('#updateReplicasText').setDisabled(true);

        this.down('#updateReplicasStatus').setVisible(false);
        this.down('#updateReplicasStatus').setText('');

        this.down('#updateReplicasLogButton').setVisible(false);

        this.down('#updateReplicasBugButton').setVisible(false);

        this.down('#fullIndexingApplyButton').setVisible(false);

        //DELETE
        this.down('#deleteTemporaryIndex').setDisabled(true);

        this.down('#indexingDeleteButton').setVisible(false);
    },

    onRunningState: function(collectionInfo) {
        // INDEXING
        this.down('#serverFormField').setDisabled(true);

        this.down('#indexingStatus').setVisible(true);
        this.down('#indexingStatus').setText(' - идет индексация...');

        this.down('#indexingLogButton').setVisible(true);

        this.down('#indexingBugButton').setVisible(false);

        this.down('#startFullIndexingButton').setVisible(false);
        this.down('#stopFullIndexingButton').setVisible(true);

        //UPDATE
        this.down('#updateReplicasText').setDisabled(true);

        this.down('#updateReplicasStatus').setVisible(false);
        this.down('#updateReplicasStatus').setText('');

        this.down('#updateReplicasLogButton').setVisible(false);

        this.down('#updateReplicasBugButton').setVisible(false);

        this.down('#fullIndexingApplyButton').setVisible(false);

        //DELETE
        this.down('#deleteTemporaryIndex').setDisabled(true);

        this.down('#indexingDeleteButton').setVisible(false);
    },

    onStopState: function(collectionInfo) {
        // INDEXING
        this.down('#serverFormField').setDisabled(true);

        this.down('#indexingStatus').setVisible(true);
        this.down('#indexingStatus').setText(' - индексация остановлена ');

        this.down('#indexingLogButton').setVisible(true);

        this.down('#indexingBugButton').setVisible(false);

        this.down('#startFullIndexingButton').setVisible(false);
        this.down('#stopFullIndexingButton').setVisible(false);

        //UPDATE
        this.down('#updateReplicasText').setDisabled(true);

        this.down('#updateReplicasStatus').setVisible(false);
        this.down('#updateReplicasStatus').setText('');

        this.down('#updateReplicasLogButton').setVisible(false);

        this.down('#updateReplicasBugButton').setVisible(false);

        this.down('#fullIndexingApplyButton').setVisible(false);

        //DELETE
        this.down('#deleteTemporaryIndex').setDisabled(false);

        this.down('#indexingDeleteButton').setVisible(true);
    },

    onErrorState: function(collectionInfo) {
        // INDEXING
        this.down('#serverFormField').setDisabled(true);

        this.down('#indexingStatus').setVisible(true);
        this.down('#indexingStatus').setText(' - ошибка индексации ');

        this.down('#indexingLogButton').setVisible(true);

        this.down('#indexingBugButton').setVisible(true);

        this.down('#startFullIndexingButton').setVisible(false);
        this.down('#stopFullIndexingButton').setVisible(false);

        //UPDATE
        this.down('#updateReplicasText').setDisabled(true);

        this.down('#updateReplicasStatus').setVisible(false);
        this.down('#updateReplicasStatus').setText('');

        this.down('#updateReplicasLogButton').setVisible(false);

        this.down('#updateReplicasBugButton').setVisible(false);

        this.down('#fullIndexingApplyButton').setVisible(false);

        //DELETE
        this.down('#deleteTemporaryIndex').setDisabled(false);

        this.down('#indexingDeleteButton').setVisible(true);
    },

    onReadyForApplyState: function(collectionInfo) {
        // INDEXING
        this.down('#serverFormField').setDisabled(true);

        this.down('#indexingStatus').setVisible(true);
        this.down('#indexingStatus').setText(' - индексация завершена ');

        this.down('#indexingLogButton').setVisible(true);

        this.down('#indexingBugButton').setVisible(false);

        this.down('#startFullIndexingButton').setVisible(false);
        this.down('#stopFullIndexingButton').setVisible(false);

        //UPDATE
        this.down('#updateReplicasText').setDisabled(false);

        this.down('#updateReplicasStatus').setVisible(false);
        this.down('#updateReplicasStatus').setText('');

        this.down('#updateReplicasLogButton').setVisible(false);

        this.down('#updateReplicasBugButton').setVisible(false);

        var fullIndexingApplyButton = this.down('#fullIndexingApplyButton');
        fullIndexingApplyButton.removeCls('indexingRefreshButton');
        fullIndexingApplyButton.addCls('startbutton');
        fullIndexingApplyButton.setTooltip('Начать обновление');
        fullIndexingApplyButton.setVisible(true);

        //DELETE
        this.down('#deleteTemporaryIndex').setDisabled(false);

        this.down('#indexingDeleteButton').setVisible(true);
    },

    onInProcessState: function(collectionInfo) {
        // INDEXING
        this.down('#serverFormField').setDisabled(true);

        this.down('#indexingStatus').setVisible(true);
        this.down('#indexingStatus').setText(' - индексация завершена ');

        this.down('#indexingLogButton').setVisible(true);

        this.down('#indexingBugButton').setVisible(false);

        this.down('#startFullIndexingButton').setVisible(false);
        this.down('#stopFullIndexingButton').setVisible(false);

        //UPDATE
        this.down('#updateReplicasText').setDisabled(false);

        this.down('#updateReplicasStatus').setVisible(true);
        this.down('#updateReplicasStatus').setText(' - идет обновление... ');

        this.down('#updateReplicasLogButton').setVisible(true);

        this.down('#updateReplicasBugButton').setVisible(false);

        this.down('#fullIndexingApplyButton').setVisible(false);

        //DELETE
        this.down('#deleteTemporaryIndex').setDisabled(false);

        this.down('#indexingDeleteButton').setVisible(true);
    },

    onErrorApplyState: function(collectionInfo) {
        // INDEXING
        this.down('#serverFormField').setDisabled(true);

        this.down('#indexingStatus').setVisible(true);
        this.down('#indexingStatus').setText(' - индексация завершена ');

        this.down('#indexingLogButton').setVisible(true);

        this.down('#indexingBugButton').setVisible(false);

        this.down('#startFullIndexingButton').setVisible(false);
        this.down('#stopFullIndexingButton').setVisible(false);

        //UPDATE
        this.down('#updateReplicasText').setDisabled(false);

        this.down('#updateReplicasStatus').setVisible(true);
        this.down('#updateReplicasStatus').setText('- ошибка обновления ');

        this.down('#updateReplicasLogButton').setVisible(true);

        this.down('#updateReplicasBugButton').setVisible(true);

        var fullIndexingApplyButton = this.down('#fullIndexingApplyButton');
        fullIndexingApplyButton
        fullIndexingApplyButton.removeCls('startbutton');
        fullIndexingApplyButton.addCls('indexingRefreshButton');
        fullIndexingApplyButton.setTooltip('Перезапустить обновление');
        fullIndexingApplyButton.setVisible(true);

        //DELETE
        this.down('#deleteTemporaryIndex').setDisabled(false);

        this.down('#indexingDeleteButton').setVisible(true);
    },

    onOkState: function(collectionInfo) {
        // INDEXING
        this.down('#serverFormField').setDisabled(true);

        this.down('#indexingStatus').setVisible(true);
        this.down('#indexingStatus').setText(' - индексация завершена ');

        this.down('#indexingLogButton').setVisible(true);

        this.down('#indexingBugButton').setVisible(false);

        this.down('#startFullIndexingButton').setVisible(false);
        this.down('#stopFullIndexingButton').setVisible(false);

        //UPDATE
        this.down('#updateReplicasText').setDisabled(false);

        this.down('#updateReplicasStatus').setVisible(true);
        this.down('#updateReplicasStatus').setText(' - обновление завершено ');

        this.down('#updateReplicasLogButton').setVisible(true);

        this.down('#updateReplicasBugButton').setVisible(false);

        var fullIndexingApplyButton = this.down('#fullIndexingApplyButton');
        fullIndexingApplyButton.removeCls('startbutton');
        fullIndexingApplyButton.addCls('indexingRefreshButton');
        fullIndexingApplyButton.setTooltip('Перезапустить обновление');
        fullIndexingApplyButton.setVisible(true);

        //DELETE
        this.down('#deleteTemporaryIndex').setDisabled(false);

    },

    refreshUI: function(collectionInfo) {
        this.refreshStartIndexingButton(this.down('#deltaStartIndexing'), collectionInfo.isCurrentlyIndexingDelta);
        this.refreshCronComponents(this.down('#deltaFlagCalendar'), this.down('#deltaEnableScheduling'), this.collectionWrapper.cronSchedule ? this.collectionWrapper.cronSchedule.enabled : false);

        this.refreshStartIndexingButton(this.down('#mainStartIndexing'), collectionInfo.isCurrentlyMerging);
        this.refreshCronComponents(this.down('#mainFlagCalendar'), this.down('#mainEnableScheduling'), this.collectionWrapper.mainCronSchedule ? this.collectionWrapper.mainCronSchedule.enabled : false);
        //this.down('#fullIndexingStateButton').setSrc(this.fullIndexingStates[collectionInfo.fullIndexingResult.fullIndexingState]);
        if(collectionInfo.fullIndexingResult.fullIndexingServer != null){
            this.server = collectionInfo.fullIndexingResult.fullIndexingServer;
            this.down('#serverFormField').setValue(this.server.name);
        }
        this.fireEvent(collectionInfo.fullIndexingResult.fullIndexingState, collectionInfo);
    },

    getCollectionWrapper: function(collectionName) {
        var collectionWrapper = null;
        Ext.Ajax.request({
            async: false,
            url:  sphinx-console.util.Utilities.SERVER_URL + "/view/collectionWrapper/" + collectionName,
            method: 'POST',
            success: function(response) {
                collectionWrapper = Ext.JSON.decode(response.responseText).result;
            }
        });
        return collectionWrapper;
    },

    getCollectionInfo: function(collectionName) {
        var collectionInfo = null;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/queryCollectionsInfoFromAgent',
            method: 'GET',
            async: false,
            success: function(response) {
                var result = Ext.JSON.decode(response.responseText);
                collectionInfo = result[collectionName];
            },
            failure: function(error) {
            }
        });
        return collectionInfo;
    },

    indexServerHandler: function(collectionName, serverStatus, taskType) {
        var action = serverStatus ? 'stopIndexing' : 'startIndexing';
        sphinx-console.app.fireEvent(action, collectionName, taskType);
    },

    startFullIndexing: function(btn){
        var server = btn.up('window').server;
        if(btn.up('window').serverIsChosen(server)){
            Ext.MessageBox.buttonText.yes = "Да";
            Ext.MessageBox.buttonText.no = "Нет";
            Ext.MessageBox.confirm('Индексация', 'Запустить полный пересбор?', function(confirmBtn){
                if(confirmBtn === 'yes'){
                    btn.up('window').onStartFullIndexing();
                }
            });
        }
    },

    stopFullIndexing: function(btn){
        var server = btn.up('window').server;
        var collectionName = btn.up('window').collectionWrapper.collection.name;
        if(btn.up('window').serverIsChosen(server)){
            Ext.MessageBox.buttonText.yes = "Да";
            Ext.MessageBox.buttonText.no = "Нет";
            Ext.MessageBox.confirm('Индексация', 'Остановить полную переиндексацию?', function(confirmBtn){
                if(confirmBtn === 'yes'){
                    sphinx-console.app.fireEvent('stopFullIndexing', collectionName, server);
                }
            });
        }
    },

    //stopApplyTask:function (btn) {
    //    var applyTaskUid = btn.up('window').collectionInfo.fullIndexingResult.applyTaskUid;
    //    Ext.MessageBox.buttonText.yes = "Да";
    //    Ext.MessageBox.buttonText.no = "Нет";
    //    Ext.MessageBox.confirm('Индексация', 'Остановить выполнение задания?', function (confirmBtn) {
    //        if (confirmBtn === 'yes') {
    //            sphinx-console.app.fireEvent('stopTask', applyTaskUid);
    //        }
    //    });
    //},

    refreshStartIndexingButton: function(btn, status) {
        btn.removeCls(status ? 'startbutton' : 'stopbutton');
        btn.addCls(status ? 'stopbutton' : 'startbutton');
        btn.setTooltip(status ? 'Остановить индексацию' : 'Запустить индексацию');
        var taskType = btn.itemId=='deltaStartIndexing' ? 'INDEXING_DELTA' : 'MERGE_DELTA';
        if(status){
            btn.setHandler(function (btn) {btn.up('window').onStopIndexing(btn,taskType)});
        }
        else{
            btn.setHandler(function (btn) {btn.up('window').onStartIndexing(btn,taskType)});
        }
    },

    refreshCronComponents: function(statusImage, btn, status) {
        btn.removeCls(status ? 'applyCalendarButton' : 'stopbutton');
        btn.addCls(status ? 'stopbutton' : 'applyCalendarButton');
        btn.setTooltip(status ? 'Остановить расписание': 'Запустить расписание');

        statusImage.removeCls(status ? 'calendarStopped' : 'calendarRunning');
        statusImage.addCls(status ? 'calendarRunning' : 'calendarStopped');
        //statusImage.title = status ? 'Расписание Остановлено': 'Расписание запущено';
    },

    onStartIndexing: function(btn, taskType) {
        var isIndexingMerging = false;
        this.indexServerHandler(this.collectionWrapper.collection.name, isIndexingMerging, taskType);
        this.refreshStartIndexingButton(btn, !isIndexingMerging);
    },

    onStopIndexing: function(btn, taskType) {
        var isIndexingMerging = true;
        this.indexServerHandler(this.collectionWrapper.collection.name, isIndexingMerging, taskType);
        this.refreshStartIndexingButton(btn, !isIndexingMerging);
    },

    onStartFullIndexing: function() {
        var collectionInfo = this.getCollectionInfo(this.collectionWrapper.collection.name);
        var isFullIndexing = collectionInfo.fullIndexingResult.fullIndexingState == 'RUNNING';
        var isApplying = collectionInfo.fullIndexingResult.fullIndexingState == 'IN_PROCESS';
        var action = isFullIndexing ? 'stopFullIndexing' : 'fullIndexing';
        if(isApplying){
            sphinx-console.app.fireEvent('stopTask', collectionInfo.taskUid);
        } else {
            sphinx-console.app.fireEvent(action, this.collectionWrapper.collection.name, this.server);
            isFullIndexing = true;
        }
        this.down('#startFullIndexingButton').setVisible(!isFullIndexing);
        this.down('#stopFullIndexingButton').setVisible(isFullIndexing);
    },

    onChangeCron: function(newValue) {
        this.collectionWrapper.cronSchedule.cronSchedule = newValue;
    },

    onChangeMainCron: function(newValue) {
        this.collectionWrapper.mainCronSchedule.cronSchedule = newValue;
    },

    onApplyCron: function(type, cronExpression) {
        var collectionWrapper = this.collectionWrapper;
        var isCronExpressionValid = sphinx-console.util.CronExpressionValidator.validate(cronExpression);
        if(isCronExpressionValid) {
            var data = new Object();
            data.collectionName = collectionWrapper.collection.name;
            data.cronExpression = cronExpression;
            data.type = type;
            sphinx-console.app.fireEvent('changeCollectionUpdateSchedule', data);
        } else {
            Ext.Msg.alert('Ошибка','Неверное выражение для расписания');
        }
    },

    onEnableDeltaCron: function() {
        var collectionWrapper = this.collectionWrapper;
        var collectionName = collectionWrapper.collection.name;
        var isSchedulingEnabled = collectionWrapper.cronSchedule ? collectionWrapper.cronSchedule.enabled : false;
        var url = isSchedulingEnabled ? '/configuration/disableScheduling/' : '/configuration/enableScheduling/';
        var enableSchedulingBtn = this.down('#deltaEnableScheduling');
        var flagCalendarBtn = this.down('#deltaFlagCalendar');
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + url + collectionName + '/INDEXING_DELTA',
            method: 'POST',
            success: function (response) {
                var status = Ext.JSON.decode(response.responseText);
                if (status.code == 0) {
                    collectionWrapper.cronSchedule.enabled = !isSchedulingEnabled;
                    th.refreshCronComponents(flagCalendarBtn, enableSchedulingBtn, collectionWrapper.cronSchedule.enabled);
                }
            }
        });
    },

    onEnableMainCron: function() {
        var collectionWrapper = this.collectionWrapper;
        var collectionName = collectionWrapper.collection.name;
        var isSchedulingEnabled = collectionWrapper.mainCronSchedule.enabled ? collectionWrapper.mainCronSchedule.enabled : false;
        var url = isSchedulingEnabled ? '/configuration/disableScheduling/' : '/configuration/enableScheduling/';
        var enableSchedulingBtn = this.down('#mainEnableScheduling');
        var flagCalendarBtn = this.down('#mainFlagCalendar');
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + url + collectionName + '/MERGE_DELTA',
            method: 'POST',
            success: function (response) {
                var status = Ext.JSON.decode(response.responseText);
                if (status.code == 0) {

                    collectionWrapper.mainCronSchedule.enabled = !isSchedulingEnabled;
                    th.refreshCronComponents(flagCalendarBtn, enableSchedulingBtn, collectionWrapper.mainCronSchedule.enabled);
                }
            }
        });
    },

    serverIsChosen: function (server) {
        var result = true;
        if(server == null) {
            Ext.MessageBox.alert('Индексация', 'Необходимо выбрать сервер');
            result = false;
        }

        return result;
    },

    fullRefreshUI: function () {
        this.collectionInfo = this.getCollectionInfo(this.collectionWrapper.collection.name);
        this.refreshUI(this.collectionInfo);
    },

    initComponent: function () {

        this.on('NOT_RUNNING', this.onNotRunningState, this);
        this.on('RUNNING', this.onRunningState, this);
        this.on('STOP', this.onStopState, this);
        this.on('ERROR', this.onErrorState, this);
        this.on('IN_PROCESS', this.onInProcessState, this);
        this.on('READY_FOR_APPLY', this.onReadyForApplyState, this);
        this.on('ERROR_APPLY', this.onErrorApplyState, this);
        this.on('OK', this.onOkState, this);

        var collectionWrapper = this.getCollectionWrapper(this.collectionWrapper.collection.name);
        this.collectionInfo =  this.getCollectionInfo(this.collectionWrapper.collection.name);
        var collectionInfo =  this.collectionInfo;
        //var useMainDeltaSchema = collectionWrapper.mainCronSchedule ? collectionWrapper.mainCronSchedule.enabled : false;
        var useMainDeltaSchema = this.collectionWrapper.collection.type == 'MAIN_DELTA';
        this.items = [
            {
                xtype: 'panel',
                items: [
                //{
                //    xtype: 'panel', //TESTING UI PANEL
                //    layout: 'hbox',
                //    items: [
                //        {
                //            xtype: 'combo',
                //            itemId: 'stateTest',
                //            fieldLabel: 'Тип(type)',
                //            mode: 'local',
                //            emptyText: 'Выберите тип',
                //            msgTarget: 'under',
                //            allowBlank: false,
                //            blankText: 'Выберите тип',
                //            editable: false,
                //            displayField: 'text',
                //            valueField: 'value',
                //            store: new Ext.data.Store({
                //                autoLoad: false, autoSync: false,
                //                fields: ['value'],
                //                data: [{text: 'NOT_RUNNING', value: 'NOT_RUNNING'},
                //                       {text: 'RUNNING', value: 'RUNNING'},
                //                       {text: 'STOP', value: 'STOP'},
                //                       {text: 'ERROR', value: 'ERROR'},
                //                       {text: 'READY_FOR_APPLY', value: 'READY_FOR_APPLY'},
                //                       {text: 'IN_PROCESS', value: 'IN_PROCESS'},
                //                       {text: 'ERROR_APPLY', value: 'ERROR_APPLY'},
                //                       {text: 'OK', value: 'OK'}
                //                ]
                //            })
                //        },
                //        {
                //            itemId: 'isIndexingTest',
                //            xtype: 'checkbox',
                //            fieldLabel: 'isIndexing'
                //        },
                //        {
                //            itemId: 'isMergingTest',
                //            xtype: 'checkbox',
                //            fieldLabel: 'isMerging'
                //        },
                //        {
                //            xtype: 'button',
                //            text: 'Get test data',
                //            handler: function(btn){
                //                var window = btn.up('window');
                //                var collectionInfo = new Object();
                //                collectionInfo.isCurrentlyIndexing = window.down('#isIndexingTest').getValue();
                //                collectionInfo.isMerging = window.down('#isMergingTest').getValue();
                //                collectionInfo.fullIndexingResult = new Object();
                //                collectionInfo.fullIndexingResult.fullIndexingState = window.down('#stateTest').getValue();
                //                collectionInfo.fullIndexingResult.fullIndexingProcessId = 777;
                //                collectionInfo.taskUid= 'taskUid';
                //                window.collectionInfo = collectionInfo;
                //                window.refreshUI(collectionInfo);
                //            }
                //        }
                //    ]
                //},

                {
                    xtype: 'button',
                    text: 'Обновить',
                    handler: function () {
                        this.up('window').refresh(collectionWrapper.collection.name);
                    }
                },
                {
                    xtype: 'panel',
                    title: 'Расписание обновления дельта индекса',
                    layout: 'hbox',
                    border: true,
                    bodyBorder : true,
                    defaults: {
                        margin: 10,
                        align: 'middle',
                        columnWidth: 0.25
                    },
                    items: [
                        {
                            itemId: 'deltaStartIndexing',
                            xtype: 'button',
                            style: "border-width: 0; background-color:white"
                        },
                        {
                            itemId: 'deltaFlagCalendar',
                            columnWidth: 0.25,
                            xtype: 'image',
                            style: "border-width: 0; background-color:white",
                            //title: collectionWrapper.cronSchedule.enabled ? 'Расписание запущено': 'Расписание Остановлено', //TODO not setting dynamically
                            cls: useMainDeltaSchema ? 'calendarRunning' : 'calendarStopped'
                        },
                        {
                            itemId: 'deltaCronExpression',
                            xtype: 'textfield',
                            allowBlank: false,
                            value: collectionWrapper.cronSchedule.cronSchedule,
                            listeners: {
                                change: function(th, newValue, oldValue, eOpts) {
                                    var window = th.up('window');
                                    window.onChangeCron(newValue);
                                    window.down('#deltaCalendar').setDisabled(!th.isDirty());
                                }
                            },
                            validator: function(value) {
                                var result = sphinx-console.util.CronExpressionValidator.validate(value);
                                this.up().down('#deltaCalendar').setDisabled(!result || !this.up().down('#deltaCronExpression').isDirty());
                                this.up().down('#deltaEnableScheduling').setDisabled(!result);
                                return result ? result : 'Неверное выражение для расписания';
                            }
                        },
                        {
                            itemId: 'deltaCalendar',
                            xtype: 'button',
                            align: 'middle',
                            disabled: true,
                            style: "border-width: 0; background-color:white",
                            cls: 'calendarButton',
                            tooltip: 'Изменить расписание',
                            handler: function (btn, func) {
                                var window = btn.up('window');
                                collectionWrapper.cronSchedule.cronSchedule = window.down('#deltaCronExpression').getValue();
                                window.down('#deltaCronExpression').originalValue =  collectionWrapper.cronSchedule.cronSchedule;
                                window.down('#deltaCronExpression').reset();
                                btn.setDisabled(true);
                                window.onApplyCron('INDEXING_DELTA', collectionWrapper.cronSchedule.cronSchedule);
                            }
                        },
                        {
                            itemId: 'deltaEnableScheduling',
                            xtype: 'button',
                            align: 'middle',
                            style: "border-width: 0; background-color:white",
                            cls: collectionWrapper.cronSchedule.enabled ? 'stopbutton' : 'applyCalendarButton',
                            handler: function (btn, func) {
                                btn.up('window').onEnableDeltaCron();
                            }
                        },
                        {
                            xtype: 'panel',
                            layout: 'vbox',
                            items: [
                                    {xtype: 'label', text: 'Предыдущее обновление'},
                                    {xtype: 'panel', items:
                                        [{xtype: 'label', text: collectionWrapper.collection.lastIndexingTime
                                                ? Ext.util.Format.date(new Date(collectionWrapper.collection.lastIndexingTime), 'd.m.Y H:i:s')
                                                : ''
                                        },
                                         {
                                             xtype: 'button',
                                             margin: 5,
                                             cls: 'indexingLogButton',
                                             style: "border-width: 0; background-color:white",
                                             handler: function (btn, func) {
                                                 var window = btn.up('window');
                                                 sphinx-console.app.fireEvent('getLogOperation', window.collectionWrapper.collection.name, 'START_INDEXING_INDEX');
                                             }
                                         }
                                        ]}
                            ]
                        },
                        {
                            xtype: 'panel',
                            layout: 'vbox',
                            items: [
                                {xtype: 'label', text: 'Следующее обновление'},
                                {xtype: 'label', text: collectionWrapper.collection.nextIndexingTime
                                    ? Ext.util.Format.date(new Date(collectionWrapper.collection.nextIndexingTime), 'd.m.Y H:i:s')
                                    : ''
                                }
                            ]

                        }
                    ]
                },
                {
                    xtype: 'panel',
                    title: 'Расписание обновления основного индекса',
                    layout: 'hbox',
                    border: true,
                    hidden: !useMainDeltaSchema,
                    bodyBorder : true,
                    defaults: {
                        margin: 10,
                        align: 'middle',
                        columnWidth: 0.25
                    },
                    items: [
                        {
                            itemId: 'mainStartIndexing',
                            xtype: 'button',
                            style: "border-width: 0; background-color:white"
                        },
                        {
                            itemId: 'mainFlagCalendar',
                            columnWidth: 0.25,
                            xtype: 'image',
                            style: "border-width: 0; background-color:white",
                            //title: collectionWrapper.mainCronSchedule.enabled ? 'Расписание запущено': 'Расписание Остановлено', //TODO not setting dynamically
                            cls: useMainDeltaSchema ? 'calendarRunning' : 'calendarStopped'
                        },
                        {
                            itemId: 'mainCronExpression',
                            xtype: 'textfield',
                            allowBlank: false,
                            value: collectionWrapper.mainCronSchedule ? collectionWrapper.mainCronSchedule.cronSchedule : null,
                            listeners: {
                                change: function(th, newValue, oldValue, eOpts) {
                                    var window = th.up('window');
                                    window.down('#mainCalendar').setDisabled(!th.isDirty());
                                    window.onChangeMainCron(newValue);
                                }
                            },
                            validator: function(value) {
                                var result = sphinx-console.util.CronExpressionValidator.validate(value);
                                this.up().down('#mainCalendar').setDisabled(!result || !this.up().down('#mainCronExpression').isDirty());
                                this.up().down('#mainEnableScheduling').setDisabled(!result);
                                return result ? result : 'Неверное выражение для расписания';
                            }
                        },
                        {
                            itemId: 'mainCalendar',
                            xtype: 'button',
                            align: 'middle',
                            disabled: true,
                            style: "border-width: 0; background-color:white",
                            cls: 'calendarButton',
                            tooltip: 'Изменить расписание',
                            handler: function (btn, func) {
                                collectionWrapper.mainCronSchedule.cronSchedule = btn.up('window').down('#mainCronExpression').getValue();
                                var window = btn.up('window');
                                window.onApplyCron('MERGE_DELTA', collectionWrapper.mainCronSchedule ? collectionWrapper.mainCronSchedule.cronSchedule : null);
                                window.down('#mainCronExpression').originalValue =  collectionWrapper.mainCronSchedule.cronSchedule;
                                window.down('#mainCronExpression').reset();
                                btn.setDisabled(true);
                            }
                        },
                        {
                            itemId: 'mainEnableScheduling',
                            xtype: 'button',
                            align: 'middle',
                            style: "border-width: 0; background-color:white",
                            cls: useMainDeltaSchema ? 'stopbutton' : 'applyCalendarButton',
                            handler: function (btn, func) {
                                btn.up('window').onEnableMainCron();
                            }
                        },
                        {
                            xtype: 'panel',
                            layout: 'vbox',
                            items: [
                                {xtype: 'label', text: 'Предыдущее обновление'},
                                {xtype: 'panel', items: [
                                    {
                                        xtype: 'label', text: collectionWrapper.collection.lastMergeTime
                                        ? Ext.util.Format.date(new Date(collectionWrapper.collection.lastMergeTime), 'd.m.Y H:i:s')
                                        : ''
                                    },
                                    {
                                        xtype: 'button',
                                        margin: 5,
                                        cls: 'indexingLogButton',
                                        style: "border-width: 0; background-color:white",
                                        handler: function (btn, func) {
                                            var window = btn.up('window');
                                            sphinx-console.app.fireEvent('getLogOperation', window.collectionWrapper.collection.name, 'START_MERGING');
                                        }
                                    }
                                ]}
                            ]
                        },
                        {
                            xtype: 'panel',
                            layout: 'vbox',
                            items: [
                                {xtype: 'label', text: 'Следующее обновление'},
                                {xtype: 'label', text: collectionWrapper.collection.nextMergeTime
                                    ? Ext.util.Format.date(new Date(collectionWrapper.collection.nextMergeTime), 'd.m.Y H:i:s')
                                    : ''
                                }
                            ]

                        }
                    ]

                },
                {
                    xtype: 'panel',
                    title: 'Полный пересбор коллекции',
                    defaults: {
                        margin: 10
                    },
                    items: [

                        {
                            xtype: 'container',
                            layout: {type: 'hbox', align: 'center'},
                            items: [
                                {
                                    itemId: 'indexingText',
                                    disabledCls: 'disabledText',
                                    xtype: 'label',
                                    text: 'Индексация: '
                                },
                                {
                                    id: 'serverFormField',
                                    //fieldLabel: 'Выбрать сервер',
                                    margin: '0 10 0 0',
                                    labelWidth: '20px',
                                    xtype : 'combo',
                                    store: Ext.create('sphinx-console.store.TypedServersStore',{processType: 'INDEXING'}),
                                    padding: '0 0 0 0',
                                    allowBlank: false,
                                    msgTarget: 'under',
                                    blankText: 'Поле обязательно для заполнения',
                                    editable: false,
                                    queryMode: 'local',
                                    allowBlank: false,
                                    blankText: 'Выберите сервер',
                                    triggerAction: 'all',
                                    displayField: 'name',
                                    valueField: 'name',
                                    listeners: {
                                        select: function(combo, records, eOpts) {
                                            var window = combo.up('window');
                                            window.server = combo.getSelection().getData();
                                            window.refreshUI(window.collectionInfo);
                                        }
                                    }
                                },
                                {
                                    itemId: 'indexingStatus',
                                    margin: '0 10 0 0',
                                    disabledCls: 'disabledText',
                                    xtype: 'label'
                                },
                                {
                                    xtype: 'button',
                                    itemId: 'indexingLogButton',
                                    cls: 'indexingLogButton',
                                    tooltip: 'Лог',
                                    style: "border-width: 0; background-color:white",
                                    visible: false,
                                    handler: function (btn) {
                                        var window = btn.up('window');
                                        var taskUid = window.collectionInfo.fullIndexingResult.indexingTaskUid;
                                        if(taskUid) {
                                            Ext.create('sphinx-console.view.logs.LogWindow', {uid: taskUid}).show();
                                        }
                                    }
                                },
                                {
                                    xtype: 'button',
                                    itemId: 'indexingBugButton',
                                    margin: '0 10 0 4',
                                    cls: 'bugbutton',
                                    tooltip: 'Показать сообщение об ошибке',
                                    style: "border-width: 0; background-color:white",
                                    visible: false,
                                    handler: function (btn) {
                                        var window = btn.up('window');
                                        var taskUid = window.collectionInfo.fullIndexingResult.indexingTaskUid;
                                        Ext.Ajax.request({
                                            url: sphinx-console.util.Utilities.SERVER_URL + '/view/taskErrorDescription/uid/' + taskUid,
                                            method: 'POST',
                                            loadScripts: true,
                                            success: function(response, opts) {
                                                var window = Ext.create('sphinx-console.view.logs.ErrorDetailWindow');
                                                window.html = response.responseText.split("\n").join("<br />");
                                                window.show();
                                            },
                                            failure: function(error) {
                                                Ext.Msg.alert('ERROR OCCURED WHILE RETRIEVING ERROR TEXT: ' + error);
                                            }
                                        });
                                     }
                                },
                                {
                                    xtype: 'panel',
                                    items: [
                                        {
                                            xtype: 'button',
                                            itemId: 'startFullIndexingButton',
                                            tooltip: 'Начать переиндексацию',
                                            margin: '0 4 0 4',
                                            cls: 'startbutton',
                                            style: "border-width: 0; background-color:white;",
                                            //visible: true,
                                            handler: function (btn) {
                                                btn.up('window').startFullIndexing(btn);
                                            }
                                        },
                                        {
                                            xtype: 'button',
                                            margin: '0 4 0 4',
                                            itemId: 'stopFullIndexingButton',
                                            tooltip: 'Остановить индексацию',
                                            style: "border-width: 0; background-color:white;",
                                            cls: 'stopbutton',
                                            visible: false,
                                            handler: function (btn) {
                                                btn.up('window').stopFullIndexing(btn);
                                            }
                                        }
                                        ]
                                }
                            ]

                        },
                        {
                            xtype: 'panel',
                            items: [
                                        {
                                            itemId: 'updateReplicasText',
                                            xtype: 'label',
                                            disabledCls: 'disabledText',
                                            margin: '0 10 0 0',
                                            text: 'Обновление поисковых нод'
                                        },
                                        {
                                            itemId: 'updateReplicasStatus',
                                            margin: '0 10 0 10',
                                            xtype: 'label',
                                            disabledCls: 'disabledText'
                                        },
                                        {
                                            xtype: 'button',
                                            margin: '0 10 0 0',
                                            itemId: 'updateReplicasLogButton',
                                            tooltip: 'Лог',
                                            cls: 'indexingLogButton',
                                            style: "border-width: 0; background-color:white",
                                            handler: function (btn) {
                                                var window = btn.up('window');
                                                var taskUid = window.collectionInfo.fullIndexingResult.applyTaskUid;
                                                if(taskUid) {
                                                    Ext.create('sphinx-console.view.logs.LogWindow', {uid: taskUid}).show();
                                                }
                                            }
                                        },
                                        {
                                            xtype: 'button',
                                            itemId: 'updateReplicasBugButton',
                                            cls: 'bugbutton',
                                            margin: '0 4 0 0',
                                            tooltip: 'Показать сообщение об ошибке',
                                            style: "border-width: 0; background-color:white",
                                            visible: false,
                                            handler: function (btn) {
                                                var window = btn.up('window');
                                                var taskUid = window.collectionInfo.fullIndexingResult.applyTaskUid;
                                                Ext.Ajax.request({
                                                    url: sphinx-console.util.Utilities.SERVER_URL + '/view/taskErrorDescription/uid/' + taskUid,
                                                    method: 'POST',
                                                    loadScripts: true,
                                                    success: function(response, opts) {
                                                        var window = Ext.create('sphinx-console.view.logs.ErrorDetailWindow');
                                                        window.html = response.responseText.split("\n").join("<br />");
                                                        window.show();
                                                    },
                                                    failure: function(error) {
                                                        Ext.Msg.alert('ERROR OCCURED WHILE RETRIEVING ERROR TEXT: ' + error);
                                                    }
                                                });
                                            }
                                        },
                                        {
                                                xtype: 'button',
                                                margin: '0 4 0 4',
                                                itemId: 'fullIndexingApplyButton',
                                                cls: 'indexingRefreshButton',
                                                tooltip: 'Запустить обновление проиндексированных данных',
                                                style: "border-width: 0; background-color:white",
                                                handler: function (btn) {
                                                    var server = btn.up('window').server;
                                                    Ext.MessageBox.buttonText.yes = "Да";
                                                    Ext.MessageBox.buttonText.no = "Нет";
                                                    Ext.MessageBox.confirm('Индексация', 'Запустить обновление проиндексированных данных?', function (confirmBtn) {
                                                        if (confirmBtn === 'yes') {
                                                            sphinx-console.app.fireEvent('applyFullIndexing', collectionWrapper.collection.name, server);
                                                            btn.up('window').fullRefreshUI();
                                                        }
                                                    });
                                                }
                                        }
                                   ]
                        },
                        {
                            xtype: 'panel',
                            defaults: {
                                margin: '0 10 0 0'
                            },
                            items: [
                                {
                                    itemId: 'deleteTemporaryIndex',
                                    xtype: 'label',
                                    disabledCls: 'disabledText',
                                    text: 'Удаление временного индекса'
                                },
                                {
                                    xtype: 'button',
                                    itemId:  'indexingDeleteButton',
                                    cls: 'indexingDeleteButton',
                                    tooltip: 'Удалить временный индекс',
                                    style: "border-width: 0; background-color:white",
                                    handler: function (btn) {
                                        Ext.MessageBox.buttonText.yes = "Да";
                                        Ext.MessageBox.buttonText.no = "Нет";
                                        Ext.MessageBox.confirm('Индексация', 'Очистить проиндексированные данные?', function(confirmBtn){
                                            if(confirmBtn === 'yes'){
                                                sphinx-console.app.fireEvent('deleteFullIndexData', collectionWrapper.collection.name);
                                                btn.up('window').fullRefreshUI();
                                            }
                                        });
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    pack: 'end',
                    items: [
                        {
                            xtype: 'button',
                            text: 'Закрыть',
                            handler: function(){
                                this.up('window').close();
                            }
                        }
                    ]
                }
            ]
            },
        ];
        this.callParent(arguments);
        this.refreshUI(collectionInfo);
    }
});