Ext.define('sphinx-console.view.collection.details.search.ModifyDistributedReplicaPortWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.addDistributedReplicaWindow',
    layout: 'fit',
    resizable: true,
    maximizable: false,
    closable: false,
    modal: true,
    data: null,
    constructor: function(data) {
        var store = Ext.getStore('ReplicasData');
        var record = store.findRecord('replicaNumber', data.replicaNumber, false, false);
        this.data = data;
        this.title = 'Редактировать ноду поиска на сервере ' + record.data.server.name,
        this.superclass.constructor.call(this);
        
        Ext.getCmp('serverPort').setValue(data.port);
        
    },
    items : [
        {
            xtype: 'panel',
            //title: 'Укажите параметры',
            layout: 'vbox',
            defaults: {
                labelWidth: 130,
                minWidth: 520 // Иначе разлетается форма при выводе сообщения об ошибке по незаполненному порту, например
            },
            items: [
                {
                    fieldLabel: 'Поисковый порт',
                    labelWidth: '20px',
                    xtype : 'textfield',
                    id: 'serverPort',
                    msgTarget: 'under',
                    //emptyText : 'Введите порт',
                    padding: '3 10 3 3',
                    allowBlank: false,
                    blankText: 'Поле обязательно для заполнения',
                    maxLength: 5,
                    maxLengthText : 'Максимальное количество символов {0}',
                    validFlag : true,
                    prevValue: '',
                    maskRe: /[0-9]/,
                    validator: function(value) {return (value > 0 && value < 65536) ? true : 'Значение должно быть в диапазоне [1, 65535]';}
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
                    dock: 'right',
                    xtype: 'button',
                    text:'Изменить',
                    handler: function(){
                        this.up('window').onModifyReplicaPort();
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
    ],
    onModifyReplicaPort : function() {
        sphinx-console.app.fireEvent('modifyDistributedReplicaPort',
            this.data.collectionName,
            this.data.replicaNumber,
            Ext.getCmp('serverPort').getValue());
        this.close();
    }
});