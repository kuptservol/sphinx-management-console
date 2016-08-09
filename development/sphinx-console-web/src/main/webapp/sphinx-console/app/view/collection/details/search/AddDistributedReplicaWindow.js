Ext.define('sphinx-console.view.collection.details.search.AddDistributedReplicaWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.addDistributedReplicaWindow',
//    width: 600,
//    height: 400,
    layout: 'fit',
    resizable: true,
    maximizable: false,
    closable: false,
    modal: true,
    title: 'Добавить новую ноду',
    collectionName: null,
    listeners: {
        close: function() {
            //sphinx-console.app.fireEvent('wizardclosed');
        }
    },
    items : [
        {
            xtype: 'form',
            layout: 'vbox',
            listeners: {
                validitychange: function(form, valid, eOpts){
                    this.up('window').down('#addButton').setDisabled(!valid);
                }
            },
            defaults: {
                labelWidth: 130,
                minWidth: 520 // Иначе разлетается форма при выводе сообщения об ошибке по незаполненному порту, например
            },
            items: [
                {
                    xtype: 'hiddenfield',
                    id: 'serverIdFormField'
                },
                {
                    fieldLabel: 'Выбрать сервер',
                    xtype : 'combo',
                    store: Ext.create('sphinx-console.store.Servers',
                        {
                            autoLoad: true,
                            pageSize: 10,
                            limit: 10,
                            page: 1,
                            start: 0
                        }),
                    id: 'serverFormField',
                    padding: '3 10 3 3',
                    allowBlank: false,
                    msgTarget: 'under',
                    blankText: 'Поле обязательно для заполнения',
                    editable: false,
                    queryMode: 'local',
                    allowBlank: false,
                    blankText: 'Выберите сервер',
                    triggerAction: 'all',
                    displayField: 'name',
                    valueField: 'name'
                },
                {
                    fieldLabel: 'Поисковый порт',
                    xtype : 'textfield',
                    id: 'searchServerPort',
                    msgTarget: 'under',
                    //emptyText : 'Введите порт сервера',
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
                    itemId: 'addButton',
                    dock: 'right',
                    xtype: 'button',
                    disabled: true,
                    text:'Добавить',
                    handler: function(){
                        this.up('window').onCreateReplica();
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
    getLastReplicaNumber: function(collectionName) {
        var result = -1;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/replicas/' + collectionName,
            method: 'GET',
            async: false,
            callback: function(options, success, response) {
                if (success) {
                    var replicas = Ext.JSON.decode(response.responseText);
                    result = replicas.reduce(function(prev, cur) {
                        return cur.number > prev.number ? cur : prev;
                    },{number: -Infinity}).number;
                } else {
                    Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
                }
            }
        });
        return result;
    },
    onCreateReplica : function() {
        sphinx-console.app.fireEvent('createDistributedReplica',
        //var uid = sphinx-console.app.getsphinx-consoleControllerReplicasController().onCreateReplica(
            this.collectionName,
            Ext.getCmp('serverFormField').getSelection().getData(),
            Ext.getCmp('searchServerPort').getValue());
//        var window = this.up('window');
//        Ext.Msg.show({
//            scope: this,
//            title:'Подтверждение',
//            msg: 'Проследить за ходом выполнения задания?',
//            buttonText: {
//                yes: 'Да',
//                no: 'Нет'
//            },
//            buttons: Ext.Msg.YESNO,
//            icon: Ext.Msg.QUESTION,
//            fn: function(buttonId, text, opt) {
//                switch (buttonId) {
//                    case 'yes':
//                        //var replicaNumber = window.getLastReplicaNumber(window.collectionName) + 1;
//                        Ext.create('sphinx-console.view.logs.LogWindow', {uid: uid}).show();
//                        break;
//                    case 'no':
//                        break;
//                }
//            }
//        });
        this.close();
    }
});