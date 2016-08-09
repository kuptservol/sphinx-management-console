Ext.define('sphinx-console.view.snippet.wizard.Step3Snippet' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.step3Snippet',
    title: 'Поля для построения сниппетов',
    requires: ['sphinx-console.util.Utilities'],
    trackResetOnLoad: true,
    layout: 'vbox',
    defaults: { // defaults are applied to items, not the container
        labelSeparator: '',
        width: '100%',
        msgTarget: 'under'
    },
    bodyPadding: 50,
    idFieldName: null,
    selectFieldNames: null,


    listeners: {
        validitychange: function(form, valid, eOpts){
            this.up('snippetWizard').fireEvent('activeFormValidityChange');
        },
        activate: function (me, opts) {
            var query = this.up('snippetWizard').down('step2Snippet').down('#mainQuery').down('textarea').getValue();
            this.up('snippetWizard').down('step3Snippet').getFieldsByQuery(query);
            this.showFields();
            if(this.idFieldName != null && this.selectFieldNames.length > 0){
                this.down('#validationField').setValue('true');
            }
            else{
                this.down('#validationField').setValue(null);
            }
            this.up('snippetWizard').fireEvent('activeFormValidityChange');
        }
    },

    initComponent: function(){
        this.items = [
            {
                xtype: 'label',
                text: 'ID записи (должен соответствовать ID в поисковом индексе) содержится в поле:',
                margin: '0 0 10 0'
            },
            {
                xtype: 'panel',
                itemId: 'idFieldPanel',
                margin: '0 0 0 30'
            },
            {
                xtype: 'label',
                text: 'Сниппеты будут построены по следующим полям:',
                margin: '40 0 10 0'
            },
            {
                xtype: 'panel',
                itemId: 'selectFieldsPanel',
                layout: 'vbox',
                margin: '0 0 0 30'
            },
            {
                xtype: 'panel',
                items: [
                    {xtype: 'textfield', hidden: 'true', itemId: 'validationField', value: null, allowBlank: false},
                ]
            }
        ];
        this.callParent(arguments);
    },

    getFieldsByQuery: function(query) {
        var th = this;
        if(query.length > 0){
            Ext.Ajax.request({
                url: sphinx-console.util.Utilities.SERVER_URL + '/view/snippetQueryFields',
                async: false,
                method: 'POST',
                jsonData: {
                    "parameter": query
                },
                success: function(response) {
                    var result = Ext.JSON.decode(response.responseText);
                    // если приходит ответ от сервера с ошибкой, выводим его и делаем шаг невалидным
                    if(result.code && result.code != 0) {
                        sphinx-console.util.ErrorMessage.showFailureResponseWindow(result);
                        th.idFieldName = null;
                        th.selectFieldNames = null;
                        th.down('#validationField').setValue(null);
                    } else {
                        th.idFieldName = result.idName;
                        th.selectFieldNames = result.snippetConfigurationFields;
                        th.down('#validationField').setValue('true');
                    }
                }
            });
        }
    },

    showFields: function() {
        // id field
        var idPanel = this.getComponent('idFieldPanel');
        idPanel.removeAll();
        idPanel.add(Ext.create('Ext.form.Label', {text: this.idFieldName, style: 'font-weight: bold'}));
        // select fields
        var selectPanel = this.getComponent('selectFieldsPanel');
        selectPanel.removeAll();
        if(this.selectFieldNames != null) {
            for(var i = 0; i < this.selectFieldNames.length; i++){
                selectPanel.add(Ext.create('Ext.form.Label', {text: this.selectFieldNames[i], style: 'font-weight: bold'}));
            }
        }
    },

    getData : function(){
        var fields = new Object();
        fields.idFieldName = this.idFieldName;
        fields.selectFieldNames = this.selectFieldNames;
        return fields;
    },

    loadData: function(data) {
        alert('загрузка данных сниппетов шаг 3')
    }

});
