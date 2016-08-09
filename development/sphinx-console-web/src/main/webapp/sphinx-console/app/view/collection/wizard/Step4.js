Ext.define('sphinx-console.view.collection.wizard.Step4', {
    extend: 'Ext.panel.Panel',
    title: 'Настройки индексации',
    requires: ['sphinx-console.view.collection.wizard.ConfigurationTemplate'],
    alias: 'widget.step4',
    layout: 'fit',    // for scroll enabling

    loadData: function(indexerConfigurationTemplate,indexConfigurationId,indexConfigurationName,indexConfigurationFilePath) {
        this.setIndexerConfigurationTemplateData(indexerConfigurationTemplate);
        this.indexConfigurationId=indexConfigurationId;
        this.indexConfigurationName=indexConfigurationName;
        this.indexConfigurationFilePath=indexConfigurationFilePath;
    },
    items: [
        {
            xtype: 'configurationtemplate',
            templatesTitle : 'Выберите шаблон для индексера Sphinx (блок indexer в sphinx.conf)',
            configurationTemplateStore:'ConfigurationTemplateIndexer',
            mainPanel: 'step4',
            shpinxConfPreviewStepData: function(me){
                return me.up('panel[alias=widget.collectionWizard]').getShpinxConfPreviewStep4Data()
            },
            templateType: 'INDEX',
            collectionType: 'SIMPLE',
            templateModel: 'sphinx-console.model.ConfigurationTemplateIndexer',
            configurationFieldsLabel:'Значения следующих параметров - path  подставляются автоматически и при добавлении в шаблоне будут проигнорированы. Значение параметров можно увидеть на предпросмотре настроек поиска.'
        }
    ],
    listeners:{
        activate: function (me, opts) {
            if(!me.up('wizard').isEdit) { //add collection
                me.down('configurationTemplateList').getStore().load();
                me.down('configurationtemplate').setConfigurationTemplateData(null);
            }
            this.down('configurationTemplateEditForm').removeBodyCls('x-border-layout-ct'); //TODO ...
        },
        deactivate: function (me, opts) {
            //me.down('[xtype=configurationtemplate]').clear();
            //me.up('wizard').fireEvent('activeFormValidityChange');
        },
        validitychange: function (form, valid, eOpts) {
            //this.up('wizard').fireEvent('activeFormValidityChange');
        }
   },
    getIndexerConfigurationTemplateData : function(){
        return this.down('[xtype=configurationtemplate]').getConfigurationTemplateData();
    },
    setIndexerConfigurationTemplateData : function(indexerConfigurationTemplateData){
        return this.down('[xtype=configurationtemplate]').setConfigurationTemplateData(indexerConfigurationTemplateData);
    },
    getIndexConfigurationId: function(){
        return this.indexConfigurationId?this.indexConfigurationId:null;
    },
    getIndexConfigurationName: function(){
        return this.indexConfigurationName?this.indexConfigurationName:null;
    },
    getIndexConfigurationFilePath: function(){
        return this.indexConfigurationFilePath?this.indexConfigurationFilePath:null;
    },
    getForm: function(){
        return this.down('[xtype=configurationtemplate]').getForm();
    }
});