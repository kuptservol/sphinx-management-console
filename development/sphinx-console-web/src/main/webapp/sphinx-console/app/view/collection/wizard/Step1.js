Ext.define('sphinx-console.view.collection.wizard.Step1' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.step1',
    title: 'Описание',
    trackResetOnLoad: true,
    delta: new Object(),

    listeners: {
        validitychange: function(form, valid, eOpts){
            this.up('wizard').fireEvent('activeFormValidityChange');
        }
    },

    getData : function(){
    	return this.getForm().getFieldValues();
    },

    getDelta : function(){
        return this.delta;
    },

    loadData: function(data) {
        this.delta = data.delta;
        this.getForm().setValues(data);
        this.down('#collectionNameField').setReadOnly(this.up('collectionWizard').isEdit);
        this.down('#collectionType').setHidden(true);
    },

    loadDistributedData: function(data) {
        
    },

    defaults: { // defaults are applied to items, not the container
        labelSeparator: '',
        width: '100%',
        msgTarget: 'under'
    },
    bodyPadding: 20,
    items: [
		{
		    xtype     : 'numberfield',
		    name      : 'id',
		    hidden    : true
		},
		{
		    xtype     : 'textfield',
		    name      : 'processingFailed',
		    value     : true,
		    hidden    : true
		},
        {
            xtype: 'textfield',
            itemId: 'collectionNameField',
            name: 'name',
            fieldLabel: 'Имя коллекции',
            emptyText: 'Введите уникальное имя коллекции',
            maxLength: 200,
            enforceMaxLength: true,
            allowBlank: false,
            blankText: 'Введите уникальное имя коллекции',
            maskRe: /[_a-zA-Z0-9]/,
            listeners: {
                validitychange: function(form, valid, eOpts){
                    this.up('wizard').fireEvent('activeFormValidityChange');
                }
            },
            validator: function(value) {
                var wizard = this.up('wizard');
                var result = false;
                if(value && this.isDirty()) {
                    Ext.Ajax.request({
                        async: false,
                        url: sphinx-console.util.Utilities.SERVER_URL + '/view/collection/' + value,
                        method: 'GET',
                        success: function (response) {
                            result = (response.responseText == "");
                        }
                    });
                } else {
                    result = true;
                }
                return result ? result : 'Коллекция с таким именем уже существует';
            }
        },
        {
            xtype     : 'textareafield',
            grow      :  true,
            name      : 'description',
            fieldLabel: 'Описание',
            maxLength: 500,
            enforceMaxLength: true//,
            //maskRe: /[_a-zA-Z0-9\u0410-\u044F]/
        },
        {
            xtype: 'combo',
            id: 'collectionType',
            itemId: 'collectionType',
            name      : 'collectionType',
            fieldLabel: 'Тип коллекции',
            editable: false,
            displayField: 'text',
            valueField: 'value',
            allowBlank: false,
            store: new Ext.data.Store({
                autoLoad: false, autoSync: false,
                fields: ['value'],
                data: [{text: 'Одиночная', value: 'SIMPLE'},{text: 'Распределенная', value: 'DISTRIBUTED'}]
            }),
            listeners: {
                select: function (combo, records, eOpts) {
                    var wizard = combo.up('wizard');
                    wizard.isDistributedCollection = (combo.getValue() != 'SIMPLE');
                }
            }
        }
    ]
});