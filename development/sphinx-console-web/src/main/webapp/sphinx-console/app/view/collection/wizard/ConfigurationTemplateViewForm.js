Ext.define('sphinx-console.view.collection.wizard.ConfigurationTemplateViewForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.configurationTemplateViewForm',
    trackResetOnLoad: true,
    hidden: true,
    record: false,
    defaults:{
        labelWidth: 120
    },
    items: [
            {
                name: 'name',
                xtype: 'displayfield',
                fieldLabel: 'Наименование'
            },
            {
                name: 'description',
                xtype: 'displayfield',
                fieldLabel: 'Описание'
            },
            {
                name: 'defaultTemplate',
                xtype: 'checkboxfield',
                boxLabel: 'Установить как шаблон по умолчанию',
                fieldLabel: ' ',
                labelSeparator: '',
                disabled: true
            },
            {
                xtype: 'fieldset',
                title: 'Параметры',
                layout: 'fit',
                forceFit: true,
                columnLines: true,
                items: [
                    {
                        xtype: 'gridpanel',
                        itemId: 'configurationFields',
                        store: 'ConfigurationFields',
                        columns: [
                            { header: 'Параметр', sortable: false, menuDisabled: true, dataIndex: 'fieldKey'},
                            { header: 'Значение', sortable: false, menuDisabled: true, dataIndex: 'fieldValue'},
                            { header: 'Комментарий', sortable: false, menuDisabled: true, dataIndex: 'fieldCommentary'}
                        ]
                    }
                ]
            }
        ]
});