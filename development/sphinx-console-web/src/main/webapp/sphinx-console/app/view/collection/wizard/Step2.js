Ext.define('sphinx-console.view.collection.wizard.Step2' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.step2',
    title: 'Источник',
    trackResetOnLoad: true,
    autoScroll: true,
    dataSource: null,
    deltaSqlQuery: new Object(),
    mainSqlQuery: new Object(),
    tableName: null,
    requires : [
        'sphinx-console.view.collection.wizard.SqlErrorWindow'
    ],
    listeners: {
        validitychange: function(form, valid, eOpts){
            this.up('wizard').fireEvent('activeFormValidityChange');
        }
    },
    getSourceConfigurationFieldsData : function(){
    	var items = Ext.getCmp('mainSourceConfigurationFields').getStore().getRange();
        var dataArr = new Array();
    	Ext.each(items, function(record){
    		record.data.configurationType = 'SOURCE';
            record.data.indexType = 'MAIN';
    		dataArr.push(record.data);
    	});

        var useTable = this.down('#mainInputSqlType').getValue().mainInputSqlTypeGroup == 0;
        this.tableName = useTable ? this.tableName : null;

        if(this.tableName == null) {
            var mainSqlQuery = new Object();
            mainSqlQuery.id = this.mainSqlQuery.id;
            mainSqlQuery.fieldKey = 'sql_query';
            mainSqlQuery.fieldValue = this.mainSqlQuery.fieldValue;
            mainSqlQuery.configurationType = 'SOURCE';
            mainSqlQuery.indexType = 'MAIN';
            dataArr.push(mainSqlQuery);
        }

        if(this.down('#useMainDeltaSchemaCheckbox').getValue()) {
            var items = Ext.getCmp('deltaSourceConfigurationFields').getStore().getRange();
            Ext.each(items, function(record){
                record.data.configurationType = 'SOURCE';
                record.data.indexType = 'DELTA';
                dataArr.push(record.data);
            });

            var deltaSqlQuery = new Object();
            deltaSqlQuery.id = this.deltaSqlQuery ? this.deltaSqlQuery.id : null;
            deltaSqlQuery.fieldKey = 'sql_query';
            deltaSqlQuery.fieldValue = this.deltaSqlQuery.fieldValue;
            deltaSqlQuery.configurationType = 'SOURCE';
            deltaSqlQuery.indexType = 'DELTA';
            dataArr.push(deltaSqlQuery);
        }

    	return dataArr;
    },

    getUseMainDeltaSchema : function(){
        return this.down('#useMainDeltaSchemaCheckbox').getValue();
    },

    getData : function(){
        return this.down('dataSourcePanel').getViewModel().getData().dataSource;
    },

    getTableName : function(){
        return this.tableName;
    },


    getKillParameters : function(){
        var killParameters =  null;
        if(this.down('#deleteRecordsAvailableCheckbox').checked) {
            killParameters =  new Object();
            killParameters.type='BUSINESS_FIELD';
            killParameters.fieldKey = this.down('#deleteFieldKey').getValue();
            killParameters.fieldValueFrom = this.down('#deleteFrom').getValue();
            killParameters.fieldValueTo = this.down('#deleteTo').getValue();
        }
        return killParameters;
    },

    findQuery : function(sourceConfigurationFields){
        var result = new Object();
        Ext.each(sourceConfigurationFields, function(configurationField){
            if(configurationField.fieldKey == 'sql_query' && configurationField.configurationType == 'SOURCE' && configurationField.indexType == 'MAIN'){
                result.mainSqlQuery = configurationField;
            }
            if(configurationField.fieldKey == 'sql_query' && configurationField.configurationType == 'SOURCE' && configurationField.indexType == 'DELTA'){
                result.deltaSqlQuery = configurationField;
            }
        });
        return result;
    },

    loadData: function(data, sourceConfigurationFields, killParameters, useMainDeltaSchema) {
        var query = this.findQuery(sourceConfigurationFields);
        this.deltaSqlQuery = query.deltaSqlQuery;
        this.down('#deltaSqlQuery').setValue(query.deltaSqlQuery ? query.deltaSqlQuery.fieldValue : null);
        this.mainSqlQuery = query.mainSqlQuery;
        this.down('#mainSqlQuery').setValue(this.mainSqlQuery.fieldValue);

        this.down('dataSourcePanel').getViewModel().setData({dataSource: data});

        //filter source configuration fields
        var mainSourceConfigurationFields = new Array();
        var deltaSourceConfigurationFields = new Array();
        Ext.each(sourceConfigurationFields, function(configurationField){
            if(configurationField.fieldKey != 'sql_query' && configurationField.indexType == 'MAIN') {
                mainSourceConfigurationFields.push(configurationField);
            }
            if(configurationField.fieldKey != 'sql_query' && configurationField.indexType == 'DELTA') {
                deltaSourceConfigurationFields.push(configurationField);
            }
        });

    	var store = Ext.getCmp('mainSourceConfigurationFields').getStore();
    	store.setData(mainSourceConfigurationFields);

        var store = Ext.getCmp('deltaSourceConfigurationFields').getStore();
        store.setData(deltaSourceConfigurationFields);

        if(killParameters) {
            this.down('#deleteFieldKey').setValue(killParameters.fieldKey);
            this.down('#deleteFrom').setValue(killParameters.fieldValueFrom);
            this.down('#deleteTo').setValue(killParameters.fieldValueTo);
        }

        var useMainDeltaSchemaCheckbox = this.down('#useMainDeltaSchemaCheckbox');
        var usingMainDeltaSchema = (useMainDeltaSchema == 'MAIN_DELTA');//this.deltaSqlQuery != null;
        useMainDeltaSchemaCheckbox.setValue(usingMainDeltaSchema);
        useMainDeltaSchemaCheckbox.handler(useMainDeltaSchemaCheckbox, usingMainDeltaSchema);

        var deleteRecordsAvailable = killParameters != null;
        var deleteRecordsAvailableCheckbox = this.down('#deleteRecordsAvailableCheckbox');
        deleteRecordsAvailableCheckbox.setValue(deleteRecordsAvailable);
        deleteRecordsAvailableCheckbox.handler(deleteRecordsAvailableCheckbox, deleteRecordsAvailable);
        this.down('#mainInputSqlType').setValue({mainInputSqlTypeGroup : 1});
        this.down('#connectionButton').fireHandler();
    },

    width: '100%',
    height: '100%',

    bodyPadding: 20,

    onSuccessTestConnection : function(panel, jsonData){
        var tables = [];
        Ext.Ajax.request({
            async: false,
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/dbTables',
            method: 'POST',
            jsonData: jsonData,
            success: function (response) {
                tables =  Ext.JSON.decode(response.responseText);
            }
        });
        var dbTable = panel.down('#dbTable');
        dbTable.setHidden(false);
        panel.down('#sourcesPanel').setHidden(false);
        dbTable.store.loadData(tables);
        var useTable = panel.down('#mainInputSqlType').getValue().mainInputSqlTypeGroup == 0;
        panel.down('#mainSqlQuery').setDisabled(useTable);
        panel.down('#dbTable').setDisabled(!useTable);

        var mainSqlQuery = panel.down('#mainSqlQuery');
        var verifySQLButton = panel.down('#verifySQLButton');
        mainSqlQuery.setHidden(false);
        verifySQLButton.setHidden(false);
        panel.down('#preview').setVisible(true);

        var fields = [];
        Ext.Array.each(tables, function (item) {
            if (item.name == dbTable.getValue()) {
                Ext.Array.each(item.columns, function (column) {
                    var exist = false;
                    Ext.each(Ext.getStore('FieldMapping').getRange(), function(record){

                        if (record.data.sourceField == column.name) {
                            exist = true;
                        }

                    });

                    if (!exist) {
                        var row = [];
                        row.push(null);
                        row.push(column.name);
                        row.push(column.type);
                        row.push(column.name);
                        row.push('Нет');
                        row.push('');
                        row.push(false);
                        fields.push(row);
                    }
                });
            }
        });
        Ext.getStore('FieldMapping').add(fields);
     },

    onFailureTestConnection : function(panel, error){
        this.down('#sourcesPanel').setHidden(true);

        var dbTable = panel.down('#dbTable');
        dbTable.down('#dbTable').setHidden(true);
        dbTable.down('#dbTable').setDisabled(true);
    },

    onChangeConnectionConfiguration : function(){
        var panel = this;
        var dbTable = panel.down('#dbTable');
        dbTable.clearValue();
        dbTable.setHidden(true);
        panel.down('#sourcesPanel').setHidden(true);
        var mainSqlQuery = panel.down('#mainSqlQuery');
        var verifySQLButton = panel.down('#verifySQLButton');
        var connectionButton = panel.down('#connectionButton');
        mainSqlQuery.setHidden(true);
        verifySQLButton.setHidden(true);
        this.up('wizard').fireEvent('activeFormValidityChange'); //this line should be executed before following lines
        dbTable.setDisabled(true);
        dbTable.clearInvalid();
        mainSqlQuery.setDisabled(true);
        connectionButton.setDisabled(false);
    },

    customSqlQueryValidator: function(value) {
        var useSql = this.down('#useSql').getValue() == 1;
        return !(useSql && !value );
    },

    deltaSqlQueryValidator: function(value) {
        var useSql = this.down('#useMainDeltaSchemaCheckbox').getValue();
        return !(useSql && !value );
    },

    //sqlWasChanged : function() {
    //    this.down('#sqlQueryFailure').setValue();
    //    return this.customSqlQueryValidator() && this.deltaSqlQueryValidator() && this.killSqlQueryValidator();
    //},

    initComponent: function() {
        sphinx-console.app.on('changeConnectionConfiguration', this.onChangeConnectionConfiguration, this);
        sphinx-console.app.on('successTestConnection', this.onSuccessTestConnection, this);
        sphinx-console.app.on('failureTestConnection', this.onFailureTestConnection, this);

        this.items = [
			{
			    id: 'sqlQueryFailure',
                name: 'sqlQueryFailure',
                xtype:  'textfield',
                hidden: true,
                readOnly: true,
                allowBlank: false
			},
            {
                id: 'dataSourcePanel',
                xtype: 'dataSourcePanel'
            },
            {
                itemId: 'sourcesPanel',
                xtype: 'panel',
                items:[
                    {
                        xtype: 'fieldset',
                        itemId: 'mainFieldSet',
                        title: 'Main',
                        layout: 'fit',
                        defaults: {
                            labelWidth: 190
                        },

                        items: [
                            {
                                xtype      : 'radiogroup',
                                itemId     : 'mainInputSqlType',
                                fieldLabel : 'Таблица/SQL-запрос',
                                defaultType: 'radiofield',
                                defaults: {
                                    flex: 1
                                },
                                layout: 'hbox',
                                items: [
                                    {
                                        itemId: 'useTable',
                                        boxLabel: 'Таблица',
                                        name: 'mainInputSqlTypeGroup',
                                        checked: true,
                                        inputValue : 0,
                                        handler: function(checkBox, checked) {
                                            var panel = checkBox.up('#mainFieldSet');
                                            panel.down('#dbTable').setDisabled(!checked);
                                            panel.down('#mainSqlQuery').setDisabled(checked);

                                            var step2 = checkBox.up('step2');
                                            var useDeltaSql = step2.down('#useMainDeltaSchemaCheckbox').getValue();
                                            var deleteRecordsAvailable = step2.down('#deleteRecordsAvailableCheckbox').getValue();
                                            var selectedTable = panel.down('#dbTable').getValue() ;
                                            if(checked) {
                                                step2.down('#sqlQueryFailure').setValue(!selectedTable || useDeltaSql || deleteRecordsAvailable ? null : 'success');
                                            }
                                        }
                                    }, {
                                        itemId: 'useSql',
                                        boxLabel: 'SQL запрос',
                                        name: 'mainInputSqlTypeGroup',
                                        inputValue : 1,
                                        handler: function(checkBox, checked) {
                                            var panel = checkBox.up('#mainFieldSet');
                                            panel.down('#mainSqlQuery').setDisabled(!checked);
                                            panel.down('#dbTable').setDisabled(checked);

                                            if(checked) {
                                                checkBox.up('step2').down('#sqlQueryFailure').setValue(null);
                                            }
                                        }
                                    }]
                            },
                            {
                                xtype: 'combo',
                                itemId: 'dbTable',
                                name: 'tableName',
                                fieldLabel: 'Таблица',
                                queryMode: 'local',
                                msgTarget: 'under',
                                emptyText: 'Выберите таблицу',
                                allowBlank: true,
                                blankText: 'Выберите таблицу',
                                triggerAction: 'all',
                                displayField: 'name',
                                valueField: 'name',
                                hidden: true,
                                editable: false,
                                disabled: true,
                                store: {autoLoad: false, autoSync: false, model: 'sphinx-console.model.DbTable'},
                                listeners: {
                                    select: function(combo, records, eOpts) {
                                        combo.up('step2').down('#sqlQueryFailure').setValue('success');
                                        var fields = [];
                                        Ext.Array.each(combo.getSelection().getData().columns, function (item) {
                                            var row = [];
                                            row.push(null);
                                            row.push(item.name);
                                            row.push(item.type);
                                            row.push(item.name);
                                            row.push('Нет');
                                            row.push('');
                                            row.push(false);
                                            fields.push(row);
                                        });
                                        Ext.getStore('FieldMapping').data.clear();
                                        Ext.getStore('FieldMapping').setData(fields);
                                        combo.up('fieldset').down('textarea').setDisabled(true);
                                        combo.up('step2').tableName = combo.getSelection().getData().name;
                                    },
                                    change: function(combo, newValue, oldValue, eOpts) {
                                        combo.up('fieldset').down('textarea').setDisabled(true);
                                    }
                                }
                            },
                            {   xtype: 'textarea',
                                itemId: 'mainSqlQuery',
                                id: 'mainSqlQuery',
                                height: 100,
                                autoScroll: true,
                                flex:80,
                                enableKeyEvents: true,
                                name: 'mainSqlQuery',
                                disabled: true,
                                fieldLabel: 'Кастомный SQL',
                                emptyText: 'Введите запрос для полного пересбора коллекции',
                                msgTarget: 'under',
                                hidden: true,
                                listeners: {
                                    'keyup': function(textarea, event) {
                                        var v = textarea.getValue();
                                        textarea.up('step2').mainSqlQuery.fieldValue = v;
                                        textarea.up('step2').tableName = null;
                                        textarea.up('step2').down('#sqlQueryFailure').setValue(null);
                                    }
                                },
                                validator: function(value) {
                                    return this.up('step2').customSqlQueryValidator(value) ? true : 'Введите запрос для полного пересбора коллекции';
                                }
                            },
                            {
                                xtype: 'fieldset', region: 'center',
                                title: 'Параметры', forceFit: true,
                                id: 'mainSourceConfigurationFieldsFieldSet',
                                hidden: false,
                                layout: {type: 'auto'},
                                items: [
                                    {
                                        xtype: 'gridpanel',region: 'center',
                                        itemId: 'mainSourceConfigurationFields',
                                        id: 'mainSourceConfigurationFields',
                                        hideHeaders : true,
                                        store: Ext.create('sphinx-console.store.ConfigurationFields'),
                                        forceFit: true,
                                        minHeight: 50,
                                        maxHeight: 80,
                                        margin: '10 10 10 10',
                                        viewConfig : {
                                            scroll:false,
                                            style:{overflow: 'auto',overflowX: 'hidden'}
                                        },
                                        autoScroll: false,
                                        columnLines: true,
                                        autoResizeColumns: true,
                                        columns: [
                                            { header: 'Параметр', sortable: false, menuDisabled: true, dataIndex: 'fieldKey'},
                                            { header: 'Значение', sortable: false, menuDisabled: true, dataIndex: 'fieldValue'},
                                            { header: 'Комментарий', sortable: false, menuDisabled: true, width: 232, dataIndex: 'fieldCommentary'},
                                            {
                                                header: 'Действия',
                                                width: 73,
                                                sortable: false, menuDisabled: true,
                                                hiddenName: 'typeID',
                                                xtype: 'actioncolumn',
                                                items: [
                                                    {
                                                        icon: 'app/resources/images/edit.png',
                                                        tooltip: 'Редактировать параметр',
                                                        handler: function (grid, rowIndex, cellIndex, item, event, record) {
                                                            var window = Ext.create('sphinx-console.view.collection.wizard.TemplateParameterWindow', {record: record});
                                                            window.show();
                                                            return;
                                                        }
                                                    },
                                                    {
                                                        icon: 'app/resources/images/delete.png',
                                                        tooltip: 'Удалить параметр',
                                                        handler: function (grid, rowIndex, cellIndex, item, event, record) {
                                                            Ext.Msg.confirm({
                                                                title: 'Предупреждение',
                                                                message: 'Удалить параметр?',
                                                                buttonText: {
                                                                    yes: 'Да',
                                                                    no: 'Нет'
                                                                },
                                                                fn: function (btn) {
                                                                    if (btn === 'yes') {
                                                                        grid.getStore().remove(record);
                                                                        grid.getStore().commitChanges();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                ]
                                            }
                                        ],
                                        listeners: {
                                            datachanged: function (me, eOpts) {
                                                me.dirty = true;
                                            }
                                        }
                                    }
                                ]
                            },
                            {xtype: 'panel', layout: {type: 'hbox', pack: 'end'},
                                width: '100%',
                                margin: '10 10 10 10',
                                items:    [{
                                    itemId: 'addMainSourceParam',
                                    xtype: 'button',
                                    text: 'Добавить параметр',
                                    handler: function (btn, event) {
                                        var window = Ext.create('sphinx-console.view.collection.wizard.TemplateParameterWindow', {store: Ext.getCmp('mainSourceConfigurationFields').getStore()});
                                        window.show();
                                    }
                                }]
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        layout: {type: 'hbox', pack: 'end'},
                        defaults: {
                            margin: '2 2 2 2'
                        },
                        width: '100%',
                        items: [{
                            xtype: 'checkbox',
                            itemId: 'useMainDeltaSchemaCheckbox',
                            labelWidth: 240,
                            fieldLabel: 'Использовать схему main и delta',
                            handler: function(checkbox, checked) {
                                var panel = checkbox.up('step2');
                                panel.down('#deltaFieldSet').setVisible(checked);
                                panel.down('#deleteRecordsFieldSet').setVisible(checked);
                                var deleteRecordsAvailableCheckbox = checkbox.up('step2').down('#deleteRecordsAvailableCheckbox');
                                deleteRecordsAvailableCheckbox.setVisible(checked);
                                panel.down('#deleteRecordsFieldSet').setVisible(checked && deleteRecordsAvailableCheckbox.checked);
                                if(checked) {
                                    panel.down('#sqlQueryFailure').setValue(null);
                                } else {
                                    //panel.down('#verifySQLButton').handler(); //TODO ...
                                }
                            }
                        }]
                    },
                    {
                        xtype: 'fieldset',
                        itemId: 'deltaFieldSet',
                        title: 'Delta',
                        layout: 'fit',
                        items: [
                            {   xtype: 'textarea',
                                itemId: 'deltaSqlQuery',
                                height: 100,
                                autoScroll: true,
                                flex:80,
                                enableKeyEvents: true,
                                fieldLabel: 'Кастомный SQL',
                                emptyText: 'Введите запрос для добавления данных',
                                msgTarget: 'under',
                                listeners: {
                                    'keyup': function(textarea, event) {
                                        var v = textarea.getValue();
                                        if(textarea.up('step2').deltaSqlQuery == null) { //TODO ...
                                            textarea.up('step2').deltaSqlQuery = new Object();
                                        }
                                        textarea.up('step2').deltaSqlQuery.fieldValue = v;
                                        textarea.up('step2').down('#sqlQueryFailure').setValue(null);
                                    }
                                },
                                validator: function(value) {
                                    return this.up('step2').deltaSqlQueryValidator(value) ?  true : 'Введите запрос для полного пересбора коллекции';
                                }
                            },
                            {
                                xtype: 'fieldset', region: 'center',
                                title: 'Параметры', forceFit: true,
                                id: 'deltaSourceConfigurationFieldsFieldSet',
                                hidden: false,
                                layout: {type: 'auto'},
                                items: [
                                    {
                                        xtype: 'gridpanel',region: 'center',
                                        itemId: 'deltaSourceConfigurationFields',
                                        id: 'deltaSourceConfigurationFields',
                                        store: Ext.create('sphinx-console.store.ConfigurationFields'),
                                        forceFit: true,          //Fit to container
                                        minHeight: 50,
                                        maxHeight: 80,
                                        margin: '10 10 10 10',
                                        hideHeaders : true,
                                        viewConfig : {
                                            scroll:false,
                                            style:{overflow: 'auto',overflowX: 'hidden'}
                                        },
                                        autoScroll: false,
                                        columnLines: true,
                                        autoResizeColumns: true,
                                        columns: [
                                            { header: 'Параметр', sortable: false, menuDisabled: true, dataIndex: 'fieldKey'},
                                            { header: 'Значение', sortable: false, menuDisabled: true, dataIndex: 'fieldValue'},
                                            { header: 'Комментарий', sortable: false, menuDisabled: true, width: 232, dataIndex: 'fieldCommentary'},
                                            {
                                                header: 'Действия',
                                                width: 73,
                                                sortable: false, menuDisabled: true,
                                                hiddenName: 'typeID',
                                                xtype: 'actioncolumn',
                                                items: [
                                                    {
                                                        icon: 'app/resources/images/edit.png',
                                                        tooltip: 'Редактировать параметр',
                                                        handler: function (grid, rowIndex, cellIndex, item, event, record) {
                                                            var window = Ext.create('sphinx-console.view.collection.wizard.TemplateParameterWindow', {record: record});
                                                            window.show();
                                                            return;
                                                        }
                                                    },
                                                    {
                                                        icon: 'app/resources/images/delete.png',
                                                        tooltip: 'Удалить параметр',
                                                        handler: function (grid, rowIndex, cellIndex, item, event, record) {
                                                            Ext.Msg.confirm({
                                                                title: 'Предупреждение',
                                                                message: 'Удалить параметр?',
                                                                buttonText: {
                                                                    yes: 'Да',
                                                                    no: 'Нет'
                                                                },
                                                                fn: function (btn) {
                                                                    if (btn === 'yes') {
                                                                        grid.getStore().remove(record);
                                                                        grid.getStore().commitChanges();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                ]
                                            }
                                        ],
                                        listeners: {
                                            datachanged: function (me, eOpts) {
                                                me.dirty = true;
                                            }
                                        }
                                    }
                                ]
                            },
                            {xtype: 'panel', layout: {type: 'hbox', pack: 'end'},
                                width: '100%',
                                margin: '10 10 10 10',
                                items: [{
                                    itemId: 'addDeltaSourceParam',
                                    xtype: 'button',
                                    text: 'Добавить параметр',
                                    handler: function (btn, event) {
                                        var window = Ext.create('sphinx-console.view.collection.wizard.TemplateParameterWindow',
                                            {store: Ext.getCmp('deltaSourceConfigurationFields').getStore()});
                                        window.show();
                                    }
                                }]
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        layout: {type: 'hbox', pack: 'end'},
                        defaults: {
                            margin: '2 2 2 2'
                        },
                        width: '100%',
                        items: [{
                            xtype: 'checkbox',
                            itemId: 'deleteRecordsAvailableCheckbox',
                            labelWidth: 220,
                            fieldLabel: 'Поддержка удаления записей',
                            handler: function(checkbox, checked) {
                                var panel = checkbox.up('step2');
                                panel.down('#deleteRecordsFieldSet').setVisible(checked);
                            }
                        }]
                    },
                    {
                        xtype: 'fieldset',
                        itemId: 'deleteRecordsFieldSet',
                        hidden: true,
                        items: [
                            {
                                xtype: 'panel',
                                items: [
                                    {
                                        xtype: 'panel',
                                        defaults: {
                                            margin: '5 20 5 20'
                                        },
                                        items: [
                                            {
                                                xtype: 'textfield',
                                                fieldLabel: 'Поле',
                                                itemId: 'deleteFieldKey',
                                                emptyText: 'Введите поле',
                                                width: 400
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'hbox',
                                        defaults: {
                                            margin: '5 20 5 20'
                                        },
                                        items: [
                                            {
                                                xtype: 'textfield',
                                                fieldLabel: 'Оставить в индексе только записи со значениями от ',
                                                labelWidth: 400,
                                                itemId: 'deleteFrom',
                                                width: 600,
                                                maskRe: /[0-9]/,
                                                emptyText: 'Начальное значение'
                                            },
                                            {
                                                xtype: 'textfield',
                                                fieldLabel: ' до ',
                                                labelWidth: 60,
                                                itemId: 'deleteTo',
                                                width: 260,
                                                maskRe: /[0-9]/,
                                                emptyText: 'Конечное значение'
                                            }
                                        ]

                                    }
                                ]
                            }
                        ]
                    },
                    {xtype: 'panel', layout: {type: 'hbox', pack: 'end'},
                        width: '100%',
                        items: [
                            {
                                itemId: 'preview',
                                xtype: 'button',
                                text: 'Предпросмотр',
                                margin: '0 10 0 10',
                                hidden: true,
                                handler: function () {

                                    var step2 = this.up('panel[alias=widget.step2]');
                                    var data = step2.up('panel[alias=widget.collectionWizard]').getShpinxConfPreviewStep2Data();

                                    Ext.Ajax.request({
                                        async: false,
                                        url: sphinx-console.util.Utilities.SERVER_URL + '/view/sphinxConfPreview',
                                        headers: {
                                            'Content-Type': 'application/json;charset=utf-8'
                                        },
                                        params: Ext.JSON.encodeValue(data, '\n'),
                                        waitTitle: 'Connecting',
                                        waitMsg: 'Creating...',
                                        method: 'POST',
                                        loadScripts: true,
                                        success: function (response, opts) {
                                            var configContent = response.responseText;

                                            if (configContent.indexOf("stackTrace") > 0) {

                                                var status = Ext.JSON.decode(response.responseText);

                                                if (status && status.code != 0) {
                                                    var message;
                                                    if (status.code == 6) {
                                                        message = "Невозможно сформировать sphinx.conf. Ошибка в SQL запросе";
                                                    } else {
                                                        message = "Невозможно сформировать sphinx.conf. Ошибка";
                                                    }
                                                    Ext.MessageBox.alert(message, status.stackTrace);

                                                    return;
                                                }
                                            }

                                            htmlReadyConfigContent = configContent.split("\n").join("<br />");
                                            htmlReadyConfigContent = htmlReadyConfigContent.split("{").join('{<div style="margin-left: 20px;">');
                                            htmlReadyConfigContent = htmlReadyConfigContent.split("}").join('</div>}');

                                            var configWindow = Ext.create('Ext.window.Window', {
                                                width: 900,
                                                height: 650,
                                                layout: 'fit',
                                                maximizable: false,
                                                modal: true,
                                                autoScroll: true,
                                                title: 'sphinx.conf',
                                                bodyStyle: "padding:10 10 0 10px;background:#FFFFFF;background-color:#FFFFFF",
                                                html: htmlReadyConfigContent,
                                                buttons: [
                                                    {
                                                        text: 'Закрыть',
                                                        handler: function () {
                                                            configWindow.close();
                                                        }
                                                    }
                                                ]
                                            });
                                            configWindow.show();
                                            return;
                                        },
                                        failure: function (error) {
                                            Ext.Msg.alert('ERROR OCCURED WHILE RETRIEVINS SPHINX CONF: ' + error);
                                        }

                                    });

                                }
                            },
                            {
                                xtype: 'button',
                                id: 'verifySQLButton',
                                width: 150,
                                itemId: 'verifySQLButton',
                                text: 'Проверить SQL',
                                hidden: true,
                                handler: function() {
                                    var panel = this.up('step2');
                                    var me = this;

                                    if (me.up('wizard')) {
                                         Ext.getCmp('sqlQueryFailure').setValue(null);
                                    }

                                    //TODO ...
                                    if ((panel.mainSqlQuery === '' && panel.tableName === '') || panel.deltaSqlQuery === '') {
                                        return;
                                    }

                                    var sourceWrapper = new Object();
                                    var dataSource = panel.getData();
                                    var useMainDeltaSchema = panel.down('#useMainDeltaSchemaCheckbox').getValue();
                                    sourceWrapper.mainSqlQuery = panel.mainSqlQuery.fieldValue;
                                    sourceWrapper.tableName = panel.tableName;
                                    sourceWrapper.deltaSqlQuery = useMainDeltaSchema ? panel.deltaSqlQuery.fieldValue : null;
                                    sourceWrapper.datasource = dataSource;
                                    var jsonData = Ext.JSON.encode(sourceWrapper);
                                    var metaData = [];

                                    Ext.Ajax.request({

                                        url: sphinx-console.util.Utilities.SERVER_URL + '/view/queryMetaData',
                                        method: 'POST',
                                        jsonData: jsonData,
                                        success: function (response) {
                                            var response = Ext.JSON.decode(response.responseText);

                                            if (response.code) {
                                                Ext.getCmp('sqlQueryFailure').setValue(null);
                                                //Ext.Msg.alert('Статус', 'Передан некорректный SQL запрос: ' + response.stackTrace);
                                                Ext.create('sphinx-console.view.collection.wizard.SqlErrorWindow', {fieldType: response.description, sqlError: response.stackTrace}).show();
                                            } else {
                                                Ext.getCmp('sqlQueryFailure').setValue("success");
                                                /**
                                                 * Если у нас уже есть мэппинг полей (коллекция редактируется),
                                                 * то новые значения нужно сравнить со старыми и использовать
                                                 * старые данные: названия полей в индексе, типы, комментарии, признак ID
                                                 */
                                                var oldFields = {};
                                                Ext.each(Ext.getStore('FieldMapping').getRange(), function (item) {
                                                    if (typeof (item.data.sourceField != 'undefined')) {
                                                        oldFields[item.data.sourceField] = item.data;
                                                    }
                                                });

                                                var fields = [];
                                                metaData = response;
                                                metaData.forEach(function(item) {
                                                    var hasOldField = typeof oldFields[item.name] != 'undefined';
                                                    var row = [];
                                                    row.push(null);
                                                    row.push(item.name);
                                                    row.push(item.type);
                                                    row.push(
                                                        hasOldField
                                                            ? oldFields[item.name].indexField
                                                            : sphinx-console.model.FieldMapping.indexFieldCleanup(item.name)
                                                    );
                                                    row.push(
                                                        hasOldField
                                                            ? oldFields[item.name].indexFieldType
                                                            : 'Нет'
                                                    );
                                                    row.push(
                                                        hasOldField
                                                            ? oldFields[item.name].indexFieldCommentary
                                                            : ''
                                                    );
                                                    row.push(
                                                        hasOldField
                                                            ? oldFields[item.name].isId
                                                            : false
                                                    );
                                                    fields.push(row);
                                                });

                                                Ext.getStore('FieldMapping').data.clear();
                                                Ext.getStore('FieldMapping').setData(fields);
                                            }
                                        }
                                    });
                                }
                            }
                        ]
                    }
                ]
            }
        ];
        this.callParent(arguments);

        this.down('#sqlQueryFailure').setValue(this.up('collectionWizard').isEdit ? 'success' : null);

        this.down('#sourcesPanel').setHidden(true);

        var deleteRecordsAvailableCheckbox = this.down('#deleteRecordsAvailableCheckbox');
        deleteRecordsAvailableCheckbox.setValue(false);
        this.down('#deleteRecordsFieldSet').setVisible(false);

        var useMainDeltaSchemaCheckbox = this.down('#useMainDeltaSchemaCheckbox');
        useMainDeltaSchemaCheckbox.setValue(false);
        useMainDeltaSchemaCheckbox.handler(useMainDeltaSchemaCheckbox, false);

        this.mainSqlQuery.fieldValue = "";
        this.deltaSqlQuery.fieldValue = "";
        Ext.getCmp('mainSourceConfigurationFields').getStore().removeAll(true);
        Ext.getCmp('deltaSourceConfigurationFields').getStore().removeAll(true);
    }
});