Ext.define('sphinx-console.view.collection.wizard.Step7' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.step7',
    title: 'Выбор сервера и настройка расписания',
    requires: ['sphinx-console.model.Server', 'sphinx-console.util.ServersTaskRunner'],
    trackResetOnLoad: true,
    layout: {
        type: 'border'
    },
    serversStore: null,
    selectedIndexServerName: null,
    selectedSearchServerName: null,
    externalAction: new Object(),
    serversTaskRunner: Ext.create('sphinx-console.util.ServersTaskRunner'),
    listeners: {
        activate: function(th) {
            this.down('#cronExpression').setValue(this.createCronExpression());
            this.down('#cronExpressionDelta').setValue(this.createCronDeltaExpression());

            this.serversStore.load({
                callback: function(records){
                    this.serversTaskRunner.startUpdateInfo(records);
                    this.selectedIndexServerName = this.selectedIndexServerName ? this.selectedIndexServerName : null;
                    this.selectedSearchServerName = this.selectedSearchServerName ? this.selectedSearchServerName : null;
                },
                scope: this
            });
            var useMainDeltaSchema = this.up('wizard').down('step2').getUseMainDeltaSchema();
            this.down('#deltaSchedule').setVisible(useMainDeltaSchema);
            this.down('#cronExpressionDelta').setVisible(useMainDeltaSchema);
            this.hideExternalAction(!useMainDeltaSchema || !this.down('#needExternalAction').getValue());

            this.down('#needExternalAction').setVisible(this.up('wizard').down('step2').getUseMainDeltaSchema());
            this.down('#isServersSelected').setValue(th.isServersSelected());
        },
        deactivate: function() {
            this.serversTaskRunner.destroy();
        },
        validitychange: function(form, valid, eOpts){
            this.up('wizard').fireEvent('activeFormValidityChange');
        }
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
                var indexServerSelected = false;
                var searchServerSelected = false;
                records.forEach(function(record) {
                    if(record.get('name') == th.selectedIndexServerName) {
                        indexServerSelected = true;
                    }
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
        return this.selectedIndexServerName != null && this.selectedSearchServerName != null ? true : '';
    },

    createCronExpression: function(){
        var minutes = this.down('#minute').getSubmitValue();
        var hours = this.down('#hour').getSubmitValue();
        var day = this.down('#day').getSubmitValue();
        var month = this.down('#month').getSubmitValue();
        var weekday = this.down('#weekDay').getSubmitValue();
        var dummy = '1';
        var dday = day ? day : weekday == '?' ? dummy : '?';
        var cronExpression = Ext.util.Format.format('0 {0} {1} {2} {3} {4}',
                                                    minutes ? minutes : dummy,
                                                    hours ? hours : dummy,
                                                    dday,
                                                    month ? month : dummy,
                                                    weekday ? weekday : dday == '?' ? 'MON' : '?');
        this.down('#cronExpression').setValue(cronExpression);
        return cronExpression;
    },

    createCronDeltaExpression: function(){
        var minutes = this.down('#minuteDelta').getSubmitValue();
        var hours = this.down('#hourDelta').getSubmitValue();
        var day = this.down('#dayDelta').getSubmitValue();
        var month = this.down('#monthDelta').getSubmitValue();
        var weekday = this.down('#weekDayDelta').getSubmitValue();
        var dummy = '1';
        var dday = day ? day : weekday == '?' ? dummy : '?';
        var cronExpression = Ext.util.Format.format('0 {0} {1} {2} {3} {4}',
            minutes ? minutes : dummy,
            hours ? hours : dummy,
            dday,
            month ? month : dummy,
            weekday ? weekday : dday == '?' ? 'MON' : '?');
        this.down('#cronExpressionDelta').setValue(cronExpression);
        return cronExpression;
    },

    hideExternalAction: function(value) {
//        alert('hideExternalAction value - ' + value);

        this.down('#externalActionCode').setHidden(value);
        this.down('#externalActionType').setHidden(value);
    },


    cronExpressionValidator: function(value) {
        var result = sphinx-console.util.CronExpressionValidator.validate(value);//this.validateCronExpression(value);
        return result ? result : 'Неверное выражение для расписания';
    },

    cronDeltaExpressionValidator: function(value) {
        var result = sphinx-console.util.CronExpressionValidator.validate(value);//this.validateCronExpression(value);
        return result ? result : 'Неверное выражение для расписания';
    },

    getIndexServerData : function(){
        return this.down('#serversGrid').store.findRecord('name', this.selectedIndexServerName, false, false).getData(true);
    },

    getSearchServerData : function(){
        return this.down('#serversGrid').store.findRecord('name', this.selectedSearchServerName, false, false).getData(true);
    },

    getExternalAction : function() {
        if(this.externalAction && this.down('#needExternalAction').getValue()) {
            this.externalAction.code = this.down('#externalActionCode').getValue();
            this.externalAction.type = this.down('#externalActionType').getValue();
        }

        return this.externalAction && this.down('#needExternalAction').getValue() ? this.externalAction : null;
    },

    loadData: function(cron, cronDelta, indexServer, searchServer, externalAction) {
    	//http://stackoverflow.com/questions/16690613/parse-crontab-line-with-javascript
        var minutes = this.down('#minute');
        var hours = this.down('#hour');
        var day = this.down('#day');
        var month = this.down('#month');
        var weekday = this.down('#weekDay');
        
        if (cron) {
	        var cronArr = cron.split(' ');
		    minutes.setValue(cronArr[1]);
            minutes.originalValue = cronArr[1];
		    hours.setValue(cronArr[2]);
            hours.originalValue = cronArr[2];
		    day.setValue(cronArr[3]);
            day.originalValue = cronArr[3];
		    month.setValue(cronArr[4]);
            month.originalValue = cronArr[4];
		    weekday.setValue(cronArr[5]);
            weekday.originalValue = cronArr[5];
        }
        this.down('#cronExpression').setValue(this.createCronExpression());

        minutes = this.down('#minuteDelta');
        hours = this.down('#hourDelta');
        day = this.down('#dayDelta');
        month = this.down('#monthDelta');
        weekday = this.down('#weekDayDelta');

        if (cronDelta) {
            var cronArr = cronDelta.split(' ');
            minutes.setValue(cronArr[1]);
            minutes.originalValue = cronArr[1];
            hours.setValue(cronArr[2]);
            hours.originalValue = cronArr[2];
            day.setValue(cronArr[3]);
            day.originalValue = cronArr[3];
            month.setValue(cronArr[4]);
            month.originalValue = cronArr[4];
            weekday.setValue(cronArr[5]);
            weekday.originalValue = cronArr[5];
        }
        this.down('#cronExpressionDelta').setValue(this.createCronDeltaExpression());

        this.selectedIndexServerName = indexServer.name;
        this.selectedSearchServerName = searchServer.name;

        this.down('#needExternalAction').setValue(externalAction != null);
        this.hideExternalAction(externalAction == null);
        if(externalAction) {
            this.externalAction = externalAction;
            this.down('#externalActionCode').setValue(externalAction.code);
            this.down('#externalActionType').setValue(externalAction.type);
        }
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

    items: [
        {
            xtype: 'panel',
            width: '100%',
            region: 'north',
            border: false,
            defaults: {
                margin: '20 50 20 50',
                width: '100%'
            },
            layout: {type : 'vbox', align: 'middle'},
            items:[
                {
                    xtype: 'label',
                    text: 'Выберите сервер поиска и сервер индексации из cписка доступных серверов и установите расписание индексации'
                }]
        },
        {
            xtype: 'panel',
            width: '40%',
            region: 'west',
            border: false,
            forceFit: true,
            layout: {
                type: 'vbox'
            },
            defaults: {
                labelSeparator: '',
                labelWidth: 150,
                margin: '5 10 0 10',
                width: '100%',
                allowBlank: false,
                blankText: 'Поле обязательно для заполнения',
                msgTarget: 'under'
            },
            items:[
                {
                    xtype: 'label',
                    text: 'Расписание в формате крон'
                },
                {
                    itemId: 'minute',
                    name: 'minute',
                    xtype: 'textfield',
                    fieldLabel: 'Минута',
                    maxLength: 100,
                    value: '*',
                    enforceMaxLength: true,
                    maskRe: /[\*\,\-0-9\/]/,
                    listeners: { change: function(value) {return this.up('step7').createCronExpression()}}
                },
                {
                    itemId: 'hour',
                    name: 'hour',
                    xtype: 'textfield',
                    fieldLabel: 'Час',
                    maxLength: 100,
                    value: '*',
                    enforceMaxLength: true,
                    maskRe: /[\*\,\-0-9\/]/,
                    listeners: { change: function(value) {return this.up('step7').createCronExpression()}}
                },
                {
                    itemId: 'day',
                    name: 'day',
                    xtype: 'textfield',
                    fieldLabel: 'День',
                    maxLength: 100,
                    value: '*',
                    enforceMaxLength: true,
                    maskRe: /[\*\,\-0-9\/LW\?]/,
                    listeners: { change: function(value) {return this.up('step7').createCronExpression()}}
                },
                {
                    itemId: 'month',
                    name: 'month',
                    xtype: 'textfield',
                    fieldLabel: 'Месяц',
                    maxLength: 100,
                    value: '*',
                    enforceMaxLength: true,
                    maskRe: /[\*\,\-0-9\/JANFEBMRPYUNLGSOCTVD]/,
                    listeners: { change: function(value) {return this.up('step7').createCronExpression()}}
                },
                {
                    itemId: 'weekDay',
                    name: 'weekDay',
                    xtype: 'textfield',
                    fieldLabel: 'День недели',
                    maxLength: 100,
                    value: '?',
                    enforceMaxLength: true,
                    maskRe: /[\*\.\-1-7\/\?L#SUNMOTEWDHFRIA]/,
                    listeners: { change: function(value) {return this.up('step7').createCronExpression()}}
                },
                {
                    itemId: 'cronExpression',
                    name: 'cronExpression',
                    xtype: 'textfield',
                    fieldLabel: 'Cron expression',
                    allowBlank: false,
                    readOnly: true,
                    validator: function(value) {return this.up('step7').cronExpressionValidator(value)}
                },
                {
                xtype: 'container',
                itemId: 'deltaSchedule',
                items: [
                        {
                            itemId: 'labelDelta',
                            xtype: 'label',
                            text: 'Расписание попадения дельты в основную часть'
                        },
                        {
                            itemId: 'minuteDelta',
                            name: 'minuteDelta',
                            xtype: 'textfield',
                            fieldLabel: 'Минута',
                            maxLength: 100,
                            value: '*',
                            enforceMaxLength: true,
                            maskRe: /[\*\,\-0-9\/]/,
                            listeners: { change: function(value) {return this.up('step7').createCronDeltaExpression()}}
                        },
                        {
                            itemId: 'hourDelta',
                            name: 'hourDelta',
                            xtype: 'textfield',
                            fieldLabel: 'Час',
                            maxLength: 100,
                            value: '*',
                            enforceMaxLength: true,
                            maskRe: /[\*\,\-0-9\/]/,
                            listeners: { change: function(value) {return this.up('step7').createCronDeltaExpression()}}
                        },
                        {
                            itemId: 'dayDelta',
                            name: 'dayDelta',
                            xtype: 'textfield',
                            fieldLabel: 'День',
                            maxLength: 100,
                            value: '*',
                            enforceMaxLength: true,
                            maskRe: /[\*\,\-0-9\/LW\?]/,
                            listeners: { change: function(value) {return this.up('step7').createCronDeltaExpression()}}
                        },
                        {
                            itemId: 'monthDelta',
                            name: 'monthDelta',
                            xtype: 'textfield',
                            fieldLabel: 'Месяц',
                            maxLength: 100,
                            value: '*',
                            enforceMaxLength: true,
                            maskRe: /[\*\,\-0-9\/JANFEBMRPYUNLGSOCTVD]/,
                            listeners: { change: function(value) {return this.up('step7').createCronDeltaExpression()}}
                        },
                        {
                            itemId: 'weekDayDelta',
                            name: 'weekDayDelta',
                            xtype: 'textfield',
                            fieldLabel: 'День недели',
                            maxLength: 100,
                            value: '?',
                            enforceMaxLength: true,
                            maskRe: /[\*\.\-1-7\/\?L#SUNMOTEWDHFRIA]/,
                            listeners: { change: function(value) {return this.up('step7').createCronDeltaExpression()}}
                        },
                        {
                            itemId: 'cronExpressionDelta',
                            name: 'cronExpressionDelta',
                            xtype: 'textfield',
                            fieldLabel: 'Cron expression',
                            allowBlank: false,
                            readOnly: true,
                            validator: function(value) {return this.up('step7').cronDeltaExpressionValidator(value)}
                        }]
                }
            ]
        },
        {
            xtype: 'panel',
            region: 'east',
            width: '60%',
            border: false,
            defaults: {
                width: '100%'
            },
            items: [
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
                            header: 'Сервер индексации',
                            align: 'center',
                            sortable: false,
                            menuDisabled: true,
                            renderer: function (value, metaData, record, rowIdx, colIdx, store) {
                                var checked = this.up('step7').selectedIndexServerName == record.get('name') ? 'checked=checked' : '';
                                var disabled = 'disabled=true';
                                if(!this.up('wizard').isEdit) {
                                    var adminProcessesCount = this.getAdminProcessesCount(record.get('id'), 'INDEX_AGENT');
                                    disabled = adminProcessesCount > 0 ? '' : 'disabled=true';
                                }
                                return '<input type="radio" name="selectedIndexServer" ' + checked + ' ' + disabled + ' onclick="onIndexServerClick(\'' + record.get('name') + '\')" />';
                            }
                        },
                        {
                            header: 'Сервер поиска',
                            align: 'center',
                            sortable: false,
                            menuDisabled: true,
                            renderer: function (value, metaData, record, rowIdx, colIdx, store) {
                                var checked = this.up('step7').selectedSearchServerName == record.get('name') ? 'checked=checked' : '';
                                var disabled = 'disabled=true';
                                if(!this.up('wizard').isEdit) {
                                    var adminProcessesCount = this.getAdminProcessesCount(record.get('id'), 'SEARCH_AGENT');
                                    disabled = adminProcessesCount > 0 ? '' : 'disabled=true';
                                }
                                return '<input type="radio" name="selectedSearchServer" ' + checked + ' ' + disabled + ' onclick="onSearchServerClick(\''+ record.get('name') +'\')" />';
                            }
                        }
                    ]
                },
                {xtype: 'textfield', hidden: 'true', itemId: 'isServersSelected', value: null, allowBlank: false},
                {   xtype: 'panel',
                    padding: '20 20 0 20',
                    itemId: 'externalActionPanel',
                    items: [
                        {
                            name: 'needExternalAction',
                            itemId: 'needExternalAction',
                            xtype: 'checkboxfield',
                            boxLabel: 'Указать действие по окончание попадания записей из дельты в основную часть',
                            fieldLabel: ' ',
                            labelSeparator: '',
                            listeners: {
                                change: function(btn, newValue) {
                                    var panel = btn.up('step7');
                                    panel.hideExternalAction(!newValue);
                                    panel.fireEvent('validitychange');
                                }
                            }
                        },
                        {
                            xtype: 'container',
                            itemId: 'externalAction',
                            items:[
                                {
                                    fieldLabel: 'Тип действия',
                                    itemId: 'externalActionType',
                                    labelWidth: '30px',
                                    hidden: true,
                                    mode: 'local',
                                    queryMode: 'local',
                                    xtype: 'combobox',
                                    displayField: 'name',
                                    valueField: 'type',
                                    lazyInit: false,
                                    triggerAction: 'all',
                                    emptyText: 'Выберите тип',
                                    editable : false,
                                    forceSelection: true,
                                    lazyRender: true,
                                    store : new Ext.data.ArrayStore({
                                        fields : ['type', 'name'],
                                        data : [
                                            ['SQL', 'SQL']//,
                                            //['SSH', 'SSH']
                                        ]
                                    }),
                                    colspan: 1,
                                    validator: function(value) {
                                        return value != '' || !this.up('step7').down('#needExternalAction').getValue() ? true : 'Выберите тип';
                                    }
                                },
                                {
                                    name: 'externalActionCode',
                                    itemId: 'externalActionCode',
                                    xtype: 'textareafield',
                                    hidden: true,
                                    fieldLabel: 'Запрос *',
                                    emptyText: 'Укажите запрос',
                                    width: '100%',
                                    validator: function(value) {
                                        return value != '' || !this.up('step7').down('#needExternalAction').getValue() ? true : 'Укажите запрос';
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});

function onSearchServerClick(name) {
	var th = Ext.ComponentQuery.query('step7')[0]
    th.selectedSearchServerName = name;
    th.down('#isServersSelected').setValue(th.isServersSelected());
    th.fireEvent('validitychange');
};

function onIndexServerClick(name) {
	var th = Ext.ComponentQuery.query('step7')[0];
    th.selectedIndexServerName = name;
    th.down('#isServersSelected').setValue(th.isServersSelected());
    th.fireEvent('validitychange');
};
