Ext.define('sphinx-console.view.collection.wizard.StepDistributed3', {
    extend: 'Ext.form.Panel',
    alias: 'widget.stepDistributed3',
    title: 'Настройки поиска',
    trackResetOnLoad: true,

    loadData: function (searchConfigurationTemplate, searchConfigurationId, searchConfigurationName, searchConfigurationFilePath, searchConfigurationPort) {
        this.setSearchСonfigurationTemplateData(searchConfigurationTemplate);
        this.searchConfigurationId = searchConfigurationId;
        this.searchConfigurationName = searchConfigurationName;
        this.searchConfigurationFilePath = searchConfigurationFilePath;
        this.setSearchConfigurationPort(searchConfigurationPort);

        this.down('#searchConfigurationPortPanel').setHidden(true);
        Ext.apply(this.down('#searchConfigurationPortPanel'), {height: 0});
        Ext.apply(this.down('#configurationtemplateWrapper'), {anchor: '100% 100%', height: '100%'});
    },
    items:[
        {
            xtype: 'container',
            layout: 'anchor',
            width: '100%',
            height: '100%',
            items:[
                {
                    xtype: 'panel',
                    anchor: '100%',
                    height: 100,
                    itemId: 'searchConfigurationPortPanel',
                    defaults: {
                        margin: '20 10 20 10',
                        width: '100%'
                    },
                    items: [
                        {
                            xtype: 'panel',
                            items: [
                                {
                                    xtype: 'textfield',
                                    fieldLabel: 'Порт SphinxQL&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color="red">*</font>',
                                    labelWrap: false,
                                    labelWidth: 'auto',
                                    itemId: 'searchConfigurationPort',
                                    emptyText: 'Введите порт',
                                    msgTarget: 'under',
                                    maxLength: 5,
                                    enforceMaxLength: true,
                                    maskRe: /[0-9]/,
                                    width: '30',
                                    validator: function (value) {
                                        var result = false;
                                        if (value && this.isDirty()) {
                                            result = true;
                                        }
                                        return result ? result : 'Введите порт';
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    layout: 'fit',
                    anchor: '100% -80',
                    items: [
                        {
                            xtype: 'configurationtemplate',
                            itemId: 'searchConfigurationTemplate',
                            height: '100%',
                            templatesTitle: 'Выберите шаблон для серчера Sphinx (блок searchd в sphinx.conf)',
                            configurationTemplateStore: 'ConfigurationTemplateDistributedSearch',
                            mainPanel: 'stepDistributed3',
                            shpinxConfPreviewStepData: function (me) {
                                return me.up('panel[alias=widget.collectionWizard]').getShpinxConfPreviewDistributedStep3Data()
                            },
                            templateType: 'SEARCH',
                            collectionType: 'DISTRIBUTED',
                            templateModel: 'sphinx-console.model.ConfigurationTemplateSearch',
                            configurationFieldsLabel: 'Значения следующих параметров - log, query_log, pid_file, binlog_path подставляются автоматически и при добавлении в шаблоне будут проигнорированы. Значение параметров можно увидеть на предпросмотре настроек поиска.'
                        }
                    ]
                }
            ]
        }
    ],
    listeners: {
        activate: function (me, opts) {
            if(!me.up('wizard').isEdit) { //add collection
                me.down('configurationTemplateList').getStore().load();
                me.down('configurationtemplate').setConfigurationTemplateData(null);
            }
            //me.down('[xtype=configurationtemplate]').refreshTemplates();
            this.down('configurationTemplateEditForm').removeBodyCls('x-border-layout-ct'); //TODO ...
        },
        deactivate: function (me, opts) {
            //me.down('[xtype=configurationtemplate]').clear();
            //me.up('wizard').fireEvent('activeFormValidityChange');
        },
        validitychange: function (form, valid, eOpts) {
            this.up('wizard').fireEvent('activeFormValidityChange');
        }
    },
    getSearchСonfigurationPort: function () {
        return this.down('#searchConfigurationPort').getValue();
    },
    getSearchСonfigurationTemplateData: function () {
        return this.down('[xtype=configurationtemplate]').getConfigurationTemplateData();
    },
    setSearchСonfigurationTemplateData: function (searchСonfigurationTemplateData) {
        return this.down('[xtype=configurationtemplate]').setConfigurationTemplateData(searchСonfigurationTemplateData);
    },
    setSearchConfigurationPort: function (searchConfigurationPort) {
        return this.down('#searchConfigurationPort').setValue(searchConfigurationPort);
    },
    getSearchConfigurationId: function () {
        return this.searchConfigurationId ? this.searchConfigurationId : null;
    },
    getSearchConfigurationName: function () {
        return this.searchConfigurationName ? this.searchConfigurationName : null;
    },
    getSearchConfigurationFilePath: function () {
        return this.searchConfigurationFilePath ? this.searchConfigurationFilePath : null;
    },
    getForm: function () {
        return this.down('[xtype=configurationtemplate]').getForm();
    }

});