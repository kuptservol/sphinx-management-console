Ext.define('sphinx-console.view.collection.wizard.ConfigurationTemplate', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.configurationtemplate',
    layout:{type: 'hbox',align: 'stretch'},
    requires: ['sphinx-console.view.collection.wizard.ConfigurationTemplateList', 'sphinx-console.view.collection.wizard.ConfigurationTemplateFormPanel'],
    currentConfigurationTemplate:  null,

    constructor: function (configuration) {
        this.configuration = configuration;
        this.superclass.constructor.call(this);
        this.currentConfigurationTemplate = null;
    },
    initComponent: function () {
        var  configuration = this.configuration;
        this.items=[
                     {
                        xtype: 'panel',
                        layout: 'fit',    // for scroll enabling
                        flex: 0.5,
                        items: [
                            {
                                xtype: 'label',
                                margin: '10 10 10 10',
                                text: configuration.templatesTitle
                            },
                            {
                                    xtype:'fieldset',
                                    margin: '20 20 0 0',
                                    title: 'Шаблоны',
                                    layout: 'fit',    // for scroll enabling
                                    items: [
                                            {xtype: 'configurationTemplateList', configuration: configuration}
                                    ]
                            }
                        ]
                    },
                     {xtype: 'configurationTemplateFormPanel', hidden: true, configuration: configuration}
                    ];
        this.callParent(arguments);
    },

    clear: function(){
        var me = this;
        Ext.each(['configurationTemplateEditForm', 'configurationTemplateViewForm'], function (name) {
            var panelFields = me.down('[itemId=' + name + ']');
            panelFields.getForm().reset();
            panelFields.getForm().setValues(Ext.create(me.configuration.templateModel).data);
            panelFields.record = null;
            panelFields.down('[itemId=configurationFields]').getStore().removeAll();
            panelFields.hide();
        });
    },
    getData : function(){
        var result;
        Ext.each(this.down('[itemId=configurationTemplate]').getStore().getData().getRange(),function(item){
            if(!!item.get('defaultTemplate')) result=item;
        });
        return result;
    },
    getDefaultTemplate: function (records) {
        var result = null;
        if(!records){
            records = this.down('configurationTemplateList').getStore().getRange()
        }
        Ext.each(records, function (item) {
            if(item.get('defaultTemplate')) {
                result = item;
            }
        });
        return result;
    },
    getDefaultTemplateData: function (records) {
        var template = this.getDefaultTemplate();
        return template ? this.getDefaultTemplate().data : null;
    },
    getConfigurationTemplateData: function () {
        return this.currentConfigurationTemplate&&
            this.currentConfigurationTemplate.isModel ? this.currentConfigurationTemplate.data : this.currentConfigurationTemplate;
    },
    setConfigurationTemplateData: function (currentConfigurationTemplate) {
        if(!this.currentConfigurationTemplate) {
            if(currentConfigurationTemplate) {
                var configurationTemplateRecord = this.down('configurationTemplateList').getStore().findRecord('id', currentConfigurationTemplate.id);
                this.currentConfigurationTemplate = configurationTemplateRecord ? configurationTemplateRecord : this.getDefaultTemplate();
            } else {
                this.currentConfigurationTemplate = this.getDefaultTemplate();
            }
            if (this.currentConfigurationTemplate) {
                this.down('configurationTemplateFormPanel').showTemplate(this.currentConfigurationTemplate);
            }
        }
    },

    getForm: function () {
        if (this.currentConfigurationTemplate) {
            if (this.currentConfigurationTemplate.systemTemplate) {
                return this.down('configurationTemplateViewForm');
            } else {
                return this.down('configurationTemplateEditForm').down('form');
            }
        }
        return Ext.create('Ext.form.Panel');
    },

    findConfigurationTemplate: function(templates, id, name){
        var result = null;
        var key;
        var value;
        if(id) {
            key = 'id';
            value = id;
        }
        if(name) {
            key = 'name';
            value = name;
        }
        if(key){
            templates.forEach(function(template){
                if(template.get(key) == value){
                    result = template;
                }
            });
        }
        return result;
    }
});
