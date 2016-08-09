Ext.define('sphinx-console.view.collection.wizard.ConfigurationTemplateEditForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.configurationTemplateEditForm',
    record: false,
    hidden: false,
    layout: 'border',
    trackResetOnLoad: true,
    forceFit: true,
    height: 562,
    width: 260,
    configuration: null,
    constructor: function (configuration) {
        this.configuration = configuration.configuration;
        this.superclass.constructor.call(this);
    },
    initComponent: function () {
        var configuration = this.configuration;
        this.items = [
                        {
                            xtype: 'form', region: 'north',
                            trackResetOnLoad: true, forceFit: false,
                            defaults:{
                                labelWidth: 120
                            },
                            listeners:{
                                validitychange: function (form, valid, eOpts) {
                                    this.up('configurationTemplateEditForm').down('#save').setDisabled(!valid);
                                    this.up('configurationTemplateEditForm').down('#saveAsNew').setDisabled(!valid);
                                }
                            },
                            items:[
                                {
                                    xtype: 'numberfield',
                                    hidden:true,
                                    name: 'id'
                                },
                                {
                                    xtype: 'textfield',
                                    itemId: 'templateName',
                                    name: 'name',
                                    fieldLabel: 'Наименование <font color="red">*</font>',
                                    allowBlank: false,
                                    blankText: 'Поле обязательно для заполнения',
                                    emptyText: 'Введите уникальное имя шаблона',
                                    msgTarget: 'under',
                                    width: '100%'
                                },
                                {
                                    name: 'description',
                                    xtype: 'textareafield',
                                    fieldLabel: 'Описание',
                                    width: '100%'
                                },
                                {
                                    name: 'defaultTemplate',
                                    xtype: 'checkboxfield',
                                    boxLabel: 'Установить как шаблон по умолчанию',
                                    fieldLabel: ' ',
                                    labelSeparator: ''
                                }
                            ]
                        },
                        {
                            xtype: 'fieldset', region: 'center',
                            title: 'Параметры', forceFit: false,
                            layout: {type: 'border'},
                            items: [
                                {
                                    xtype: 'label', region: 'north',
                                    text: configuration.configurationFieldsLabel,
                                    style: {
                                        background: 'white'
                                    }
                                },
                                {
                                    xtype: 'gridpanel',region: 'center',
                                    itemId: 'configurationFields',
                                    store: Ext.create('sphinx-console.store.ConfigurationFields'),
                                    forceFit: true,          //Fit to container
                                    columnLines: true,
                                    autoResizeColumns: true,
                                    autoScroll: true,
                                    tbar: ['->',                                                {
                                        text: 'Добавить параметр',
                                        handler: function (btn) {
                                            var window = Ext.create('sphinx-console.view.collection.wizard.TemplateParameterWindow', {store: btn.up('gridpanel').getStore()});
                                            window.show();
                                            return;
                                        }
                                    }],
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
                        {
                            xtype: 'toolbar', region: 'south',
                            ui: 'footer',
                            items: [
                                {
                                    xtype: 'container',
                                    flex: 1
                                },
                                {
                                    itemId: 'preview',
                                    xtype: 'button',
                                    text: 'Предпросмотр',
                                    handler: function () {
                                        //this.up('panel').hide();
                                    	var url;
                                    	if (Ext.getCmp('collectionType').getValue() == 'DISTRIBUTED') {
                                    		url = sphinx-console.util.Utilities.SERVER_URL + '/view/distributedSphinxConfPreview';
                                    	} else {
                                    		url = sphinx-console.util.Utilities.SERVER_URL + '/view/sphinxConfPreview';
                                    	}

                                        var data = configuration.shpinxConfPreviewStepData(this);


                                        Ext.Ajax.request({
                                            async: false,
                                            url: url,
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
                                    itemId: 'save',
                                    xtype: 'button',
                                    hidden: true,
                                    text: 'Сохранить',
                                    handler: function (button, event) {
                                        var th = button.up('configurationTemplateFormPanel');
                                        var step = button.up('configurationtemplate');
                                        if(th.validateTemplateName(true)) {
                                            var configurationFields = [],
                                                storeTemplatesFields = step.down('[itemId=configurationFields]').getStore();

                                            storeTemplatesFields.each(function (rec, idx) {
                                                var data = rec.data;
                                                data.id = null;
                                                configurationFields.push(data);
                                            });
                                            var defaultTemplate = step.getDefaultTemplate(); //need before set data
                                            var data = th.getForm().getFieldValues();
                                            var record = step.down('configurationTemplateList').getStore().findRecord('id', data.id);
                                            record.set(data);
                                            record.set('configurationFields', configurationFields);
                                            step.down('configurationTemplateFormPanel').saveTemplate(record, defaultTemplate);
                                            th.showTemplate(record);
                                        }
                                    }
                                },
                                {
                                    itemId: 'saveAsNew',
                                    xtype: 'button',
                                    text: 'Сохранить как новый',
                                    handler: function () {
                                        var step = this.up('[alias=widget.configurationtemplate]');
                                        var th = step.down('configurationTemplateFormPanel');
                                        if (th.validateTemplateName()) {
                                            var storeTemplateFields = step.down('[itemId=configurationFields]').getStore(),
                                                defaultTemplate = step.getDefaultTemplate(), configurationFields = [];
                                            var data = th.getForm().getFieldValues();
                                            data.collectionType = configuration.collectionType;
                                            data.type = configuration.templateType;
                                            storeTemplateFields.each(function (record) {
                                                var data = record.data;
                                                data.id = null;
                                                configurationFields.push(data);
                                            });
                                            data.configurationFields = configurationFields;
                                            th.saveTemplate(data, defaultTemplate);
                                        }
                                        //step.up('wizard').fireEvent('activeFormValidityChange');
                                    }
                                }
                            ]
                        }
                     ];
        this.callParent(arguments);
    }
});