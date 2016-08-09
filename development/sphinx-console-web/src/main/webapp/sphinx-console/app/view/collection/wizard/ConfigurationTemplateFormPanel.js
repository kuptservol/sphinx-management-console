Ext.define('sphinx-console.view.collection.wizard.ConfigurationTemplateFormPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.configurationTemplateFormPanel',
    itemId: 'configurationTemplateFormPanel',
    requires: ['sphinx-console.view.collection.wizard.TemplateParameterWindow',
                'sphinx-console.view.collection.wizard.ConfigurationTemplateEditForm','sphinx-console.view.collection.wizard.ConfigurationTemplateViewForm'],
    bodyPadding: 4,
    flex: 0.5,
    layout: 'fit',    // for scroll enabling
    configuration: null,
    constructor: function (configuration) {
        this.configuration = configuration.configuration;
        this.superclass.constructor.call(this);
    },
    initComponent: function () {
        this.items = [
                        {itemId: 'configurationTemplateEditForm', xtype: 'configurationTemplateEditForm', configuration: this.configuration},
                        {itemId: 'configurationTemplateViewForm', xtype: 'configurationTemplateViewForm'}
                     ];
        this.callParent(arguments);
    },
    showTemplate: function (configurationTemplate) {
        //var previousRow = this.up('configurationtemplate').currentConfigurationTemplate;
        this.up('configurationtemplate').currentConfigurationTemplate = configurationTemplate;
        var isSystemTemplate = configurationTemplate.get('systemTemplate');
        var form = isSystemTemplate ? this.down('configurationTemplateViewForm') : this.down('configurationTemplateEditForm');
        form.getForm().setValues(configurationTemplate.data);
        form.down('gridpanel').getStore().loadData(configurationTemplate.data.configurationFields ? configurationTemplate.data.configurationFields: []);
        form.down('gridpanel').getStore().dirty = false;

        var record = this.up('configurationtemplate').down('configurationTemplateList').getStore().findRecord('name', configurationTemplate.data.name);
        //if(previousRow) previousRow.commit(); //call renderer to update checkbox
        //if(record) record.commit();
        if(record) {// otherwise it's new template
            var id = this.configuration.templateType + '_selectedTemplate_' + record.get('id'); //render not always clear 'checked=checked' with previous row (mb cause is async call)
            if(Ext.get(id)) { //for first call/ Needs call after rendering
                Ext.get(id).dom.checked = true;
            }
        }
        if(!isSystemTemplate) {
            this.down('[itemId=save]').setVisible(record);
        }
        this.down('configurationTemplateEditForm').setVisible(!isSystemTemplate);
        this.down('configurationTemplateViewForm').setVisible(isSystemTemplate);
    },

    isDirtyDataTable: function () {
        return this.down('gridpanel').getStore().dirty;
    },
    getForm: function(){
        return this.down('form').getForm();
    },

    saveTemplate: function(template,defaultTemplate){
        var step = this.up(), me = step,
            store = step.down('configurationTemplateList').getStore(),
            model;
        var th = this;
        if(template.isModel) {
            template.modified = template.data;
            model = template;
        }else{
            model = store.createModel(template);
            model.set('id',null);
            store.add(model);
            model.modified={};
            model.phantom=true;
        }
        var templateId = model.id;
        var templateName = model.data.name;
        store.sync({
            scope: this, callback: function () {
                if (defaultTemplate != null && model.get("defaultTemplate") && model.get('id') != defaultTemplate.id){ // to update default template
                    defaultTemplate.set("defaultTemplate", false);
                    defaultTemplate.modified = defaultTemplate.data;
                    store.sync({scope: this, callback: function () {
                        store.load({scope: this,callback: function (records, operation, success) {
                                if(success) {
                                    var record = step.findConfigurationTemplate(records, templateId, templateName);
                                    th.showTemplate(record);
                                }
                            }
                        });
                    }});
                }else{
                    store.load({scope: this,callback: function (records, operation, success) {
                        if(success) {
                            var record = step.findConfigurationTemplate(records, templateId, templateName);
                            th.showTemplate(record);
                        }
                    }});
                }
            }
        });
    },

    validateTemplateName: function(needCheckDirty) {
        var result = true;
        var field = this.down('#templateName');
        var needValidateName = true;
        if(needCheckDirty) {
            needValidateName = field.isDirty()
        }
        if(needValidateName) {
            Ext.Ajax.request({
                async: false,
                url: sphinx-console.util.Utilities.SERVER_URL + '/view/template/',
                useDefaultXhrHeader: false,
                headers: { 'Content-Type': 'application/json' },
                jsonData: {"name": field.getSubmitValue(), "type": this.configuration.templateType},
                method: 'POST',
                success: function (response) {
                    if(response.responseText != "") {
                        result = false;
                        field.markInvalid('Шаблон с таким именем уже существует');
                    }
                }
            });
        }
        return result;
    }
});