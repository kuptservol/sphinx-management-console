Ext.define('sphinx-console.view.collection.wizard.DataSourceWindow', {
    extend: 'Ext.window.Window',
    title : 'Сохранение источника данных',
    store: 'DataSources',
    modal: true,
    closable: false,
    closeAction: 'destroy',
    requires : [
        'sphinx-console.view.collection.wizard.DataSourceViewModel'
    ],
    viewModel: {
        type: 'dataSourceSaveForm'
    },
    items : [
        {
            xtype: 'panel',
            title: 'Название источника данных',
            layout: 'table',
            width: '100%',
            items: [
                {
                    bind: '{dataSource.name}',
                    fieldLabel: 'Имя <font color="red">*</font>',
                    labelWidth: '20px',
                    xtype : 'textfield',
                    id: 'dataSourceNameFormField',
                    emptyText : 'Введите имя источника данных',
                    padding: '3 10 3 3',
                    allowBlank: false,
                    blankText: 'Поле обязательно для заполнения'
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            items:[{xtype: 'container',flex: 1},
                {
                    itemId: 'addButton',
                    dock: 'right',
                    xtype: 'button',
                    text:'Сохранить',
                    handler: function(btn) {
                        var window = btn.up('window');
                        var newDataSourceName = window.down('#dataSourceNameFormField').getValue();
                        sphinx-console.app.fireEvent('dataSourceSave', newDataSourceName);
                        window.close();
                    }
                },{
                    dock: 'right',
                    xtype: 'button',
                    text: 'Отмена',
                    handler:function(btn){
                        btn.up('window').close();
                    }
                }
            ]
        }
    ]
});
