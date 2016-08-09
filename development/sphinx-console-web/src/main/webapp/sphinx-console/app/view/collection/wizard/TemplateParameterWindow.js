Ext.define('sphinx-console.view.collection.wizard.TemplateParameterWindow', {
    extend: 'Ext.window.Window',
    closable: false,
    maximizable: false,
    modal: true,
    title: 'Добавить параметр шаблона',
    record: null,
    store: null,
    isEdit: false,
    layout: 'fit',
    width: 800,

    constructor: function(configuration) {
        this.superclass.constructor.call(this);
        if(configuration ) {
            if(this.record = configuration.record) {
                this.title = 'Редактировать параметр шаблона';
                this.isEdit = true;
            }
            this.store = configuration.store;
        }
    },


    items:[{
        xtype: 'form',
        layout: {type: 'vbox', align: 'stretch'},
        default: {
            labelWidth: 120,
            width: '100%'
        },
        padding: '5 5 5 5',
        items: [{
            xtype:'hiddenfield',
            name: 'id'
        },{
            name: 'fieldKey',
            fieldLabel: 'Параметр <font color="red">*</font>',
            xtype : 'textfield',
            emptyText : 'Введите название параметра',
            allowBlank: false,
            msgTarget: 'under',
            blankText: 'Поле обязательно для заполнения',
            maxLength: 255,
            enforceMaxLength: true
        },{
            name: 'fieldValue',
            fieldLabel: 'Значение <font color="red">*</font>',
            xtype : 'textarea',
            emptyText : 'Введите значение параметра',
            allowBlank: false,
            msgTarget: 'under',
            blankText: 'Поле обязательно для заполнения'
        },{
            name: 'fieldCommentary',
            fieldLabel: 'Комментарий',
            xtype : 'textarea',
            emptyText : 'Введите комментарий',
            maxLength: 500,
            enforceMaxLength: true
        }]
    }],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            items:[{xtype: 'container',flex: 1},
                {
                    xtype: 'button',
                    text: 'Отмена',
                    handler:function(button){
                        button.up('window').close();
                    }
                },{
                    xtype: 'button',
                    itemId: 'btnSaveParam',
                    //disabled: true,
                    text: 'Сохранить',
                    handler: function(button){
                        var window = button.up('window');
                        if(window.isEdit) {
                            window.editTemplateParameter(window);
                        } else {
                            window.addTemplateParameter(window);
                        }
                        window.close();
                    }
                }
            ]
        }
    ],

    addTemplateParameter: function(window) {
        var form = window.down('form').getForm();
        window.store.add(form.getFieldValues());
        window.store.commitChanges();
        window.store.dirty=true;
    },

    editTemplateParameter: function(window) {
        window.record.set(window.down('form').getForm().getFieldValues());
        window.record.modified = window.down('form').getForm().getFieldValues();
        window.record.commit();
        if(window.record.store) window.record.store.dirty=true;
    },

    listeners:{
        show:function(me, opts) {
            if (me.isEdit) {
                me.down('form').getForm().setValues(this.record.data);
            }
            /*Ext.each(me.query('field'), function (field) {
                field.on('change', function () {
                    me.down('[itemId=btnSaveParam]').setDisabled(me.down('form').getForm().hasInvalidField());
                });
            });*/
        }
    }
});