Ext.define('sphinx-console.view.collection.wizard.ConfigurationTemplateList', {
    extend: 'Ext.grid.GridPanel',
    alias: 'widget.configurationTemplateList',
    forceFit: true,          //Fit to container
    columnLines: true,
    autoResizeColumns: true,
    autoScroll: true,
    padding: '3 3 14 3',
    configuration: null,
    tbar: ['->',                                                {
        text: 'Добавить шаблон',
        handler: function (btn) {
            var panel = btn.up('configurationtemplate').down('configurationTemplateFormPanel');
            var store = btn.up('configurationTemplateList').getStore();
            var model = store.createModel(panel.configuration.templateModel);
            model.data = {id: -1, name: '', defaultTemplate: '', description: ''};
            model.modified={};
            model.phantom=true;
            panel.showTemplate(model);
            panel.record = false;
        }
    }],

    listeners: {
        rowclick: function(th, record, tr, rowIndex, e, eOpts ) {
            var form = th.up('configurationtemplate').down('configurationTemplateFormPanel');
            var isDirty = form.getForm().isDirty() || form.isDirtyDataTable();
            if (record.data.name != th.up('configurationtemplate').currentConfigurationTemplate.data.name && isDirty) {
                Ext.Msg.show({
                    title: 'Предупреждение',
                    message: 'Введенные данные будут потеряны. Продолжить?',
                    buttonText: {
                        yes: 'Да',
                        no: 'Нет'
                    },
                    buttons: Ext.Msg.YESNO,
                    icon: Ext.Msg.QUESTION,
                    fn: function (btn) {
                        if (btn === 'yes') {
                            form.showTemplate(record);
                        }
                    }
                });
            } else {
                form.showTemplate(record);
            }
        }
    },

    initComponent: function () {
        var  configuration = this.configuration;
        this.store = configuration.configurationTemplateStore;
        this.columns=[
                        {
                            header: "ID",
                            dataIndex: 'id',
                            hidden: true
                        },
                        {
                            header: 'Выбран', sortable: false, menuDisabled: true,
                            width: 74,
                            renderer: function (value, metaData, record, rowIdx, colIdx, store) {
                                            var step = this.up('[alias=widget.configurationtemplate]');
                                            var configurationTemplate = step.currentConfigurationTemplate;
//                                            var checked = '';
//                                            if (configurationTemplate) {

                                                var checked = (configurationTemplate.data.id == record.get('id') ? 'checked=checked' : '');
//                                            } else {
//                                                if (record.get('defaultTemplate')) {
//                                                    //if(record.data.id) step.setConfigurationTemplateData(record.data);
//                                                    checked = 'checked=checked';
//                                                }
//                                            }
                                            var id = configuration.templateType + '_selectedTemplate_' + record.get('id');//'radio';//Ext.id();//
                                            return '<input type="radio" ' + checked +' name="' + configuration.templateType + '_selectedTemplate" id="' + id + '">';
                                        }
                        },
                        { header: 'Шаблон', sortable: false, menuDisabled: true, cellWrap: true, renderer: function (v, m, r) {
                            return r.data.name + (r.get('defaultTemplate') ? '<br/><font color="#00bfff">Установлен по умолчанию</font>' : '') + (r.get('systemTemplate') ? '<br/><font color="#00bfff">Системный шаблон</font>' : '');
                        }},
                        {
                            header: 'Действия',
                            width: 73, sortable: false, menuDisabled: true,
                            xtype: 'actioncolumn',
                            items: [
                                {
                                    icon: 'app/resources/images/template.png',
                                    getClass: function (value, meta, record) {
                                        if (!record.get('systemTemplate')) {
                                            return 'x-hidden-visibility';
                                        }
                                    },
                                    tooltip: 'Просмотреть шаблон',
                                    handler: function (grid, rowIndex, cellIndex, item, event, record) {
                                        Ext.each(['configurationTemplateEditForm', 'configurationTemplateViewForm'], function (name) {
                                            var panel = grid.up('[alias=widget.configurationtemplate]').down('[itemId=' + name + ']');
                                            panel.hide();
                                            if (name == 'configurationTemplateViewForm') {
                                                panel.getForm().setValues(record.data);
                                                panel.record = record;
                                                panel.show();
                                            }
                                        });
                                    }
                                },
                                {
                                    icon: 'app/resources/images/delete.png',
                                    tooltip: 'Удалить шаблон',
                                    getClass: function (value, meta, record) {
                                        if (record.get('systemTemplate')) {
                                            return 'x-hidden-visibility';
                                        }
                                    },
                                    handler: function (grid, rowIndex, cellIndex, item, event, record) {
                                        var step = grid.up('[alias=widget.configurationtemplate]');
                                        Ext.Msg.show({
                                            title: 'Предупреждение',
                                            message: 'Удалить шаблон?',
                                            buttonText: {
                                                yes: 'Да',
                                                no: 'Нет'
                                            },
                                            buttons: Ext.Msg.YESNO,
                                            icon: Ext.Msg.QUESTION,
                                            fn: function (btn) {
                                                if (btn === 'yes') {
                                                    var th = step.down('configurationTemplateList');
                                                    th.validateTemplateUsing(record.get('id'),function(){
                                                        var template = step.currentConfigurationTemplate;
                                                        grid.getStore().remove(record);
                                                        grid.getStore().sync({
                                                            scope: this,
                                                            callback: function (batch,options) {
                                                                grid.getStore().load({
                                                                    scope: this,
                                                                    callback: function (records, operation, status) {
                                                                        // check that value was deleted
                                                                        var isNotDeleted = step.findConfigurationTemplate(records, record.get('id'));
                                                                        if (!isNotDeleted && step.currentConfigurationTemplate.id == record.get('id')){
                                                                            template = step.getDefaultTemplate();
                                                                            if(!template) {
                                                                                template = th.getStore().first();
                                                                            }
                                                                        }
                                                                        step.currentConfigurationTemplate = template;
                                                                        step.down('configurationTemplateFormPanel').showTemplate(template);
                                                                        //step.up('wizard').fireEvent('activeFormValidityChange');
                                                                    }});
                                                            }});
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            ]
                        }
                     ];
        this.callParent(arguments);
    },

    validateTemplateUsing: function(templateId,callback){
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/collection/names/use/template/' + templateId,
            success: function (response) {
                if(response.responseText) {
                    var names = Ext.JSON.decode(response.responseText);
                    if(names.length>0){
                        Ext.Msg.alert('Статус','Невозможно удалить шаблон - он используется для коллекции : ' + names.join(','));
                    }else{
                        callback();
                    }
                }else{
                    callback();
                }
            }
        });
    }
 });
