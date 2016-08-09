Ext.define('sphinx-console.view.collection.wizard.Step6', {
    extend: 'Ext.form.Panel',
    title: 'Настройки конфигурации индекса',
    requires: ['sphinx-console.view.collection.wizard.ConfigurationTemplate'],
    alias: 'widget.step6',
    layout: 'fit',    // for scroll enabling

    loadData: function (configurationTemplateConfiguration) {
        this.setConfigurationTemplateConfiguration(configurationTemplateConfiguration);
    },

    items: [
        {
            xtype: 'configurationtemplate',
            templatesTitle: 'Выберите шаблон для конфигурации индекса (блок index в sphinx.conf)',
            configurationTemplateStore: 'ConfigurationTemplateConfiguration',
            mainPanel: 'step6',
            shpinxConfPreviewStepData: function (me) {
                return me.up('panel[alias=widget.collectionWizard]').getShpinxConfPreviewStep6Data();
            },
            templateType: 'CONFIGURATION',
            collectionType: 'SIMPLE',
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
            //me.down('[xtype=configurationtemplate]').refreshTemplates();
        },
        deactivate: function (me, opts) {
            //me.down('[xtype=configurationtemplate]').clear();
            //me.up('wizard').fireEvent('activeFormValidityChange');
        },

        validitychange: function (form, valid, eOpts) {
            //this.up('wizard').fireEvent('activeFormValidityChange');
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