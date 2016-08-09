Ext.define('sphinx-console.view.collection.wizard.StepDistributed4', {
    extend: 'Ext.form.Panel',
    alias: 'widget.stepDistributed4',
    title: 'Настройки конфигурации индекса',
    requires: ['sphinx-console.view.collection.wizard.ConfigurationTemplate'],
    trackResetOnLoad: true,

    loadData: function (configurationTemplateConfiguration) {
        this.setConfigurationTemplateConfiguration(configurationTemplateConfiguration);
    },

    items: [
        {
            xtype: 'configurationtemplate',
            templatesTitle: 'Выберите шаблон для конфигурации индекса (блок index в sphinx.conf)',
            configurationTemplateStore: 'ConfigurationTemplateDistributedConfiguration',
            mainPanel: 'stepDistributed4',
            shpinxConfPreviewStepData: function (me) {
                return me.up('panel[alias=widget.collectionWizard]').getShpinxConfPreviewDistributedStep4Data();
            },
            templateType: 'CONFIGURATION',
            collectionType: 'DISTRIBUTED',
            templateModel: 'sphinx-console.model.ConfigurationTemplateConfiguration',
            configurationFieldsLabel:''
        }
    ],
    listeners: {
        activate: function (me, opts) {
            if(!me.up('wizard').isEdit) { //add collection
                me.down('configurationTemplateList').getStore().load();
                me.down('configurationtemplate').setConfigurationTemplateData(null);
            }
            this.down('configurationTemplateEditForm').removeBodyCls('x-border-layout-ct'); //TODO ...
        }
    },
    getData: function () {
        return this.down('[xtype=configurationtemplate]').getData();
    },
    getConfigurationTemplateConfiguration: function () {
        return this.down('[xtype=configurationtemplate]').getConfigurationTemplateData();
    },
    setConfigurationTemplateConfiguration: function (сonfigurationTemplateConfiguration) {
        return this.down('[xtype=configurationtemplate]').setConfigurationTemplateData(сonfigurationTemplateConfiguration);
    },
    getForm: function () {
        return this.down('[xtype=configurationtemplate]').getForm();
    }
});