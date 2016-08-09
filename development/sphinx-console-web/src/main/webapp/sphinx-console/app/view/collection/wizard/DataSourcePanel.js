Ext.define('sphinx-console.view.collection.wizard.DataSourcePanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.dataSourcePanel',
    layout: 'vbox',
    width: '100%',
    //requires : [
    //    'sphinx-console.view.collection.wizard.DataSourceViewModel'
    //],
    viewModel: {
        type: 'dataSourceSaveForm'
    },
    listeners: {
        validitychange: function(form, valid, eOpts){
            this.down('#connectionButton').setDisabled(!valid) ;
            //if(!this.up('step2').down('#dbTable').isHidden() && valid) {
            //    this.up('wizard').fireEvent('activeFormValidityChange');
            //}
        }
    },
    defaults: { // defaults are applied to items, not the container
        labelSeparator: '',
        margin: '10 0 10 0',
        labelWidth: 190,
        width: '100%'
    },

    disableOdbcFields: function(value) {
        this.down('#odbcDsn').setDisabled(value);
        this.down('#odbcDsn').setVisible(!value);
    },

    disableJdbcFields: function(value) {
        this.down('#sqlHost').setDisabled(value);
        this.down('#sqlPort').setDisabled(value);
        this.down('#sqlUser').setDisabled(value);
        this.down('#sqlPassword').setDisabled(value);
    },

    disableFields: function() {
        /*var disabled = (this.down('#connectionType').getSubmitValue() == 'ORACLE');
        this.down('#dbName').setDisabled(disabled);
        this.disableJdbcFields(disabled);
        this.disableOdbcFields(!disabled);*/
    	
    	if (this.down('#connectionType').getSubmitValue() == 'ORACLE') {
    		this.down('#dbName').setDisabled(false);
            this.disableJdbcFields(false);
            this.disableOdbcFields(false);
    	} else {
    		this.down('#dbName').setDisabled(false);
            this.disableJdbcFields(false);
            this.disableOdbcFields(true);
    	}
    },

    disableAllFields: function(value) {
        this.down('#dataSourceId').setDisabled(value);
        this.down('#dataSourceName').setDisabled(value);
        this.down('#connectionType').setDisabled(value);
        this.down('#dbName').setDisabled(value);
        this.disableJdbcFields(value);
//        this.disableOdbcFields(value);
    },

    onDataSourceSave: function() {
        var dataSource = this.getViewModel().getData().dataSource;
        //dataSource.name = dataSourceName;
        var store = Ext.getStore('DataSources');
        //var record = store.findRecord('name', dataSourceName);
        //if(record != null) {
        //    alert('Источник данных с таким именем уже существует. Выберите другое имя.');
        //}
        if(dataSource.id <= 0) {
            dataSource.id = null;
        }
        var jsonData = Ext.JSON.encode(dataSource);
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/dataSource/save',
            method: 'POST',
            jsonData: jsonData,
            success: function (response) {
                store.load({
                    scope: this,
                    callback: function(records, operation, success) {
                        var record = store.findRecord('name', dataSource.name);
                        th.down('#dataSourceId').setValue(record.getId());
                    }
                });
            },
            failure: function (error) {
            }
        });

        //this.down('#connectionButton').fireHandler();
    },

    initComponent: function() {
        sphinx-console.app.on('dataSourceSave', this.onDataSourceSave, this);

        this.items = [
            {
                xtype: 'label',
                text: 'Выберите имеющийся источник данных или укажите параметры подключения и выберите таблицу в качестве источника',
                margin: '30 0 30 0'
            },
            {
                bind: '{dataSource.id}',
                xtype: 'combo',
                itemId: 'dataSourceId',
                fieldLabel: 'Источник данных',
                emptyText: 'Создать новый источник данных',
                allowBlank: true,
                editable: false,
                msgTarget: 'under',
                blankText: 'Создайте новый источник данных',
                width: '100%',
                displayField: 'name',
                valueField: 'id',
                store: Ext.getStore('DataSources'),
                listeners: {
                    select: function(combo, record, eOpts) {
                        var panel = combo.up('dataSourcePanel');
                        var data = combo.getStore().getById(combo.getValue()).data;
                        panel.getViewModel().setData({dataSource: data});
                        //panel.down('#saveDataSourceButton').setDisabled(false);
                        panel.disableFields();
                    }//,
                    //change: function(combo, newValue, oldValue, eOpts) {
                    //    if(!newValue) {
                    //        var panel = combo.up('dataSourcePanel');
                    //        panel.getViewModel().getData().dataSource.id = null;
                    //        //panel.down('#saveDataSourceButton').setDisabled(true);
                    //    }
                    //}
                }
            },
            {
                xtype: 'textfield',
                fieldLabel: 'Имя источника данных',
                itemId: 'dataSourceName',
                name: 'name',
                bind: '{dataSource.name}',
                emptyText: 'Введите имя источника данных',
                msgTarget: 'under',
                allowBlank: false,
                blankText: 'Введите имя источника данных'
            },
            {
                bind: '{dataSource.type}',
                xtype: 'combo',
                itemId: 'connectionType',
                name: 'type',
                fieldLabel: 'Тип (type)',
                mode: 'local',
                emptyText: 'Выберите тип',
                msgTarget: 'under',
                allowBlank: false,
                blankText: 'Выберите тип',
                editable: false,
                width: '100%',
                displayField: 'text',
                valueField: 'value',
                store: new Ext.data.Store({
                    autoLoad: true, autoSync: true,
                    fields: ['value'],
                    data: [{text: 'mssql', value: 'MSSQL'},{text: 'mysql', value: 'MYSQL'},{text: 'oracle', value: 'ORACLE'},{text: 'pgsql', value: 'PGSQL'}]
                }),
                listeners: {
                    select: function(combo, records, eOpts) {
                        var panel = combo.up('dataSourcePanel');
                        panel.disableFields();
                        panel.down('#connectionButton').setDisabled(panel.getForm().hasInvalidField());

                        var isDisabled = !(combo.getValue() === 'MYSQL' || combo.getValue() === 'PGSQL' || combo.getValue() === 'MSSQL' || combo.getValue() === 'ORACLE');
                        //Ext.getCmp('mainSourceConfigurationFieldsFieldSet').setHidden(isDisabled);
                        //Ext.getCmp('deltaSourceConfigurationFieldsFieldSet').setHidden(isDisabled);
                    },
                    change: function(combo, newValue, oldValue, eOpts) {
                        var isDisabled = !(combo.getValue() === 'MYSQL' || combo.getValue() === 'PGSQL' || combo.getValue() === 'MSSQL' || combo.getValue() === 'ORACLE');
                        //Ext.getCmp('mainSourceConfigurationFieldsFieldSet').setHidden(isDisabled);
                        //Ext.getCmp('deltaSourceConfigurationFieldsFieldSet').setHidden(isDisabled);
                    }
                }
            },
            {
                bind: '{dataSource.host}',
                xtype: 'textfield',
                itemId: 'sqlHost',
                name: 'host',
                disabled: true,
                fieldLabel: 'Адрес (sql_host)',
                emptyText: 'Введите адрес',
                msgTarget: 'under',
                maxLength: 30,
                enforceMaxLength: true,
                maskRe: /[\.\-_a-zA-Z0-9]/,
                allowBlank: false
            },
            {
                bind: '{dataSource.user}',
                xtype: 'textfield',
                fieldLabel: 'Пользователь (sql_user)',
                itemId: 'sqlUser',
                name: 'user',
                disabled: true,
                emptyText: 'Введите логин',
                msgTarget: 'under',
                maxLength: 30,
                enforceMaxLength: true,
                maskRe: /[\.\-_a-zA-Z0-9]/,
                allowBlank: false
            },
            {
                bind: '{dataSource.password}',
                xtype: 'textfield',
                fieldLabel: 'Пароль (sql_pass)',
                itemId: 'sqlPassword',
                name: 'password',
                disabled: true,
                emptyText: 'Введите пароль',
                msgTarget: 'under',
                inputType: 'password',
                maxLength: 30,
                enforceMaxLength: true,
                maskRe: /[\.\-_a-zA-Z0-9]/,
                allowBlank: false
            },
            {
                bind: '{dataSource.sqlDb}',
                xtype: 'textfield',
                fieldLabel: 'Имя базы (sql_db)',
                itemId: 'dbName',
                name: 'sqlDb',
                disabled: true,
                emptyText: 'Введите имя базы',
                msgTarget: 'under',
                maxLength: 30,
                enforceMaxLength: true,
                maskRe: /[\.\-_a-zA-Z0-9]/,
                allowBlank: false
            },
            {
                bind: '{dataSource.port}',
                xtype: 'textfield',
                fieldLabel: 'Порт (sql_port)',
                itemId: 'sqlPort',
                name: 'port',
                disabled: true,
                emptyText: 'Введите порт',
                msgTarget: 'under',
                maxLength: 5,
                enforceMaxLength: true,
                maskRe: /[0-9]/,
                width: '30',
                allowBlank: false
            },
            {
                bind: '{dataSource.odbcDsn}',
                xtype: 'textfield',
                fieldLabel: 'ODBC DSN (odbc_dsn)(* На узле индексатора должен быть установлен пакет unixODBC и настроена конфигурация ODBC)',
                itemId: 'odbcDsn',
                name: 'odbcDsn',
                disabled: true,
                hidden: true,
                emptyText: 'Введите ODBC DSN',
                msgTarget: 'under',
                maxLength: 100,
                enforceMaxLength: true,
                maskRe: /[\.\-_a-zA-Z0-9]/,
                allowBlank: false
            },
            {xtype: 'container', layout: {type: 'hbox', pack: 'end'},
                defaults: {
                    margin: '0 0 0 20'
                },
                items: [//{
                //    xtype: 'button',
                //    itemId: 'saveDataSourceButton',
                //    text: 'Сохранить источник данных',
                //    disabled: true,
                //    margin: '0 0 0 400',
                //    handler: function(btn) {
                //        var window = Ext.create('sphinx-console.view.collection.wizard.DataSourceWindow');
                //        window.getViewModel().setData(btn.up('dataSourcePanel').getViewModel().getData());
                //        window.show();
                //    }
                //},
                    {
                        xtype: 'label',
                        itemId: 'connectionLabel',
                        text: 'Установка соединения...',
                        hidden: true
                    },
                    {
                        xtype: 'button',
                        itemId: 'changeConnectionButton',
                        text: 'Изменить настройки подключения',
                        hidden: true,
                        handler: function(btn) {
                            btn.setHidden(true);
                            var panel = btn.up('dataSourcePanel');
                            panel.disableAllFields(false);
                            panel.disableFields(false);
                            panel.down('#connectionLabel').setHidden(true);

                            var connectionButton = panel.down('#connectionButton');
                            connectionButton.setDisabled(false);

                            sphinx-console.app.fireEvent('changeConnectionConfiguration');
                        }
                    },
                    {
                        xtype: 'button',
                        itemId: 'connectionButton',
                        text: 'Тестировать соединение',
                        disabled: true,
                        handler: function(btn) {
                            var panel = btn.up('dataSourcePanel');
                            //panel.down('#saveDataSourceButton').setDisabled(false);
                            var label = panel.down('#connectionLabel');
                            label.setText('Установка соединения...');
                            label.show();
                            panel.disableAllFields(false);

                            /**
                             * Это костыль, который решает проблему:
                             * В данной форме биндинг переменных почему-то странно иногда срабатывал - в модель
                             * значения полей передавались обрезанными.
                             * Штатными средствами биндинга обойтись не удалось, пришлось сделать заполнение
                             * словаря вручную.
                             *
                             * После обновления версии ExtJs надо проверить, возможно будет стабильно
                             * работать стандартный механизм:
                             * var dataSource = panel.getViewModel().getData().dataSource
                             *
                             * Тогда к нему надо будет вернуться.
                             */

                            //убираем костыль, если все будет работать стабильно, убрать закомментаренный код и комментарии
                            var dataSource = panel.getViewModel().getData().dataSource;
/*                            for (var k in dataSource ) {
                                var field = panel.getForm().findField(k);
                                if (field) {
                                    var val = panel.getForm().findField(k).getModelData()[k];
                                    if (val) {
                                        dataSource[k] = val;
                                    }
                                }
                            }
*/
                            //var dataSource = panel.getViewModel().getData().dataSource;//.create('sphinx-console.model.Datasource', panel.getViewModel().getData().dataSource);
                            //if(panel.down('#dataSourceId').getValue() == null) dataSource.set('id', null);
                            var jsonData = Ext.JSON.encode(dataSource);//dataSource.getData(true));

                            Ext.Ajax.request({
                                url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/checkDBConnection',
                                method: 'POST',
                                jsonData: jsonData,
                                success: function (response) {
                                    var status = Ext.JSON.decode(response.responseText);
                                    var result = status && status.code == 0;

                                    panel.down('#connectionLabel').setText(result ? 'Соединение установлено' : 'Соединение не установлено');
                                    panel.down('#changeConnectionButton').setHidden(!result);
                                    //panel.down('#saveDataSourceButton').setDisabled(!result);
                                    panel.disableAllFields(result);
                                    if (result) {
                                        //panel.onDataSourceSave();
                                        sphinx-console.app.fireEvent('successTestConnection', panel.up('step2'), jsonData);
                                    } else {
                                        panel.disableFields();
                                    }
                                },
                                failure: function (error) {
                                    panel.down('#connectionLabel').setText('Соединение не установлено');
                                    panel.down('#changeConnectionButton').setHidden(true);
                                    panel.disableFields();
                                    sphinx-console.app.fireEvent('failureTestConnection', panel, error);
                                }
                            });
                        }

                    }]
            }
        ];
        this.callParent(arguments);
    }
});