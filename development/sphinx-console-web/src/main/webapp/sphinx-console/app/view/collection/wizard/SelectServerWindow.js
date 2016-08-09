Ext.define('sphinx-console.view.collection.wizard.SelectServerWindow', {
    extend: 'Ext.window.Window',
    title : 'Выбрать сервер',
    closable: false,
    forceFit: true,
    closeAction: 'destroy',
    modal: true,
    onSelect: null,
    layout: 'vbox',
    items : [
        {
            id: 'server',
            //fieldLabel: 'Выбрать сервер',
            labelWidth: '20px',
            xtype : 'combo',
            store: Ext.create('sphinx-console.store.TypedServersStore',{processType: 'INDEXING'}),
            padding: '3 10 3 3',
            width: '100%',
            msgTarget: 'under',
            blankText: 'Поле обязательно для заполнения',
            editable: false,
            queryMode: 'local',
            allowBlank: false,
            blankText: 'Выберите сервер',
            triggerAction: 'all',
            displayField: 'name',
            valueField: 'name'
        }, {
            xtype: 'label',
            padding: '3 10 3 3',
            width: '100%',
            html: '<p>На выбранном сервере будет произведена полная переиндексация коллекции. </p>' +
                  '<p>После ее завершения необходимо вручную запустить обновление проиндексированных данных на индексовых нодах.</p>'
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
                    text:'Выбрать',
                    handler: function(){
                        var server = Ext.getCmp('server').getSelection().getData();
                        //sphinx-console.app.fireEvent('applyFullIndexing', collectionWrapper.collection.name, server);
                        this.up('window').onSelect(server);
                        this.up('window').close();
                    }
                },{
                    dock: 'right',
                    xtype: 'button',
                    text: 'Отмена',
                    handler:function(){
                        this.up('window').close();
                    }
                }
            ]
        }
    ]
});
