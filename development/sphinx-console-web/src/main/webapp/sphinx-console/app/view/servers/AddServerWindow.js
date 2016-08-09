Ext.define('sphinx-console.view.servers.AddServerWindow', {
    extend: 'Ext.window.Window',
    title : 'Добавить сервер',
    closable: false,
    closeAction: 'destroy',
    layout: 'fit',
    width: 390,
    modal: true,

    checkPort: function(ip, type, port, statusElementId) {
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/adminProcessStatus',
            method: 'POST',
            useDefaultXhrHeader: false,
            headers: { 'Content-Type': 'application/json' },
            jsonData: {
                "server":{"ip": ip},
                "type": type,
                "port": port
            },
            method: 'POST',
            success: function(response, opts) {
                var statusElement = th.down('#' + statusElementId);
                switch(response.responseText){
                    case '"RUNNING"' :
                        statusElement.setSrc('app/resources/images/accept.png');
                        //statusElement.removeCls('');
                        //statusElement.removeCls('');
                        //statusElement.addCls('');
                        break;
                    case '"STOPPED"' :
                        statusElement.setSrc('app/resources/images/theme2/error.png');
                        //statusElement.removeCls('');
                        //statusElement.removeCls('');
                        //statusElement.addCls('');
                        break;
                }
            }
        });
    },



    createServer: function(name, ip) {
        var server = new Object();
        server.name = name;
        server.ip = ip;
        return server;
    },

    createAdminProcess: function(type, port, server) {
        var adminProcess = new Object();
        adminProcess.type = type;
        adminProcess.port = port;
        adminProcess.server = server;
        return adminProcess;
    },

    updateStatus: function(imageId, status) {

    },

    items : [
        {
            xtype: 'panel',
            layout: {type: 'vbox', align: 'stretch'},
            margin: '10 10 10 10',
            items: [
                {
                    xtype: 'panel',
                    layout: 'vbox',
                    margin: '5 10 0 0',
                    defaults: {
                        margin: '5 5 5 5'
                    },
                    items: [
                        {
                            xtype: 'hiddenfield',
                            id: 'serverIdFormField'
                        },
                        {
                            fieldLabel: 'Имя <font color="red">*</font>',
                            labelWidth: 80,
                            xtype : 'textfield',
                            id: 'serverNameFormField',
                            emptyText : 'Введите имя сервера',
                            padding: '3 10 3 3',
                            allowBlank: false,
                            blankText: 'Поле обязательно для заполнения',
                            maxLength: 30,
                            maxLengthText : 'Максимальное количество символов {0}',
                            validFlag : true,
                            prevValue: '',
                            validator : function() {
                                return this.validFlag;
                            },
                            listeners : {
                                'change' : function(textfield, newValue, oldValue) {
                                    var me = this;
                                    var prevValue = this.up('window').record?this.up('window').record.data.name:"";
                                    if(newValue&&newValue.length>0&&prevValue!=newValue){
                                        Ext.Ajax.request({
                                            url: Ext.getStore('Servers').getModel().getProxy().api.read,
                                            method: 'POST',
                                            useDefaultXhrHeader: false,
                                            headers: { 'Content-Type': 'application/json' },
                                            jsonData: {
                                                filter:[{property:"uniqueServerName",value:newValue}]
                                            },
                                            success: function(response) {
                                                me.validFlag = (Ext.JSON.decode(response.responseText).list.length!=0?"Сервер с таким именем уже существует":true);
                                                me.validate();
                                            }
                                        });
                                    }
                                }
                            }
                        },
                        {
                            fieldLabel: 'IP-адрес <font color="red">*</font>',
                            labelWidth: 80,
                            xtype : 'textfield',
                            id: 'serverIpFormField',
                            emptyText : 'Введите IP-адрес сервера',
                            padding: '3 10 3 3',
                            allowBlank: false,
                            blankText: 'Поле обязательно для заполнения',
                            //minLength: 12,
                            //minLengthText : 'Количество вводимых символов должно быть точно равно {0}',
                            validFlag : true,
                            prevValue: '',
                            validator : function() {
                                return this.validFlag;
                            },
                            listeners : {
                                'change' : function(textfield, newValue, oldValue) {
                                    var me = this;
                                    var prevValue = this.up('window').record?this.up('window').record.data.ip:"";
                                    if(newValue&&newValue.length>0&&prevValue!=newValue){
                                        Ext.Ajax.request({
                                            url: Ext.getStore('Servers').getModel().getProxy().api.read,
                                            method: 'POST',
                                            useDefaultXhrHeader: false,
                                            headers: { 'Content-Type': 'application/json' },
                                            jsonData: {
                                                filter:[{property:"uniqueServerIp",value:newValue.replace(/_/g,'')}]
                                            },
                                            success: function(response) {
                                                me.validFlag = (Ext.JSON.decode(response.responseText).list.length!=0?"Сервер с таким IP-адресом уже существует":true);
                                                me.validate();
                                            }
                                        });
                                    }
                                }
                            },
                            regex: /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
                            regexText: "Не верный формат IP",
                            maskRe: /[\d\.]/i
                        }/*,
                         {
                         xtype: 'button',
                         labelWidth: '20px',
                         text: 'Тестировать соединение',
                         padding: '3 10 3 3',
                         handler: function() {

                         }
                         }*/
                    ]
                },
                {
                    xtype: 'label',
                    text: 'Процессы:',
                    padding: '5 20 0 0'
                },
                {
                    xtype: 'container',
                    margin: '5 10 0 0',
                    items:[
                        {
                            xtype: 'panel',
                            layout: 'hbox',
                            defaults: {
                                margin: '5 0 5 5'
                            },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    itemId: 'coordinatorCheckbox',
                                    boxLabel: 'Координатор',
                                    inputValue: 'COORDINATOR',
                                    style: 'vertical-align: middle;',
                                    width: 120
                                },{
                                    itemId: 'coordinatorPort',
                                    xtype: 'textfield',
                                    value: 8888,
                                    width: 70,
                                    style: 'vertical-align: middle;'
                                },{
                                    xtype: 'button',
                                    text: 'Проверить',
                                    handler: function(btn) {
                                        var window = btn.up('window');
                                        window.down('#coordinatorStatus').setSrc('app/resources/images/loading.gif');
                                        window.checkPort(window.down('#serverIpFormField').getValue(), 'COORDINATOR', window.down('#coordinatorPort').getValue(), 'coordinatorStatus');
                                    }
                                }, {
                                    itemId: 'coordinatorStatus',
                                    xtype: 'image',
                                    style: 'vertical-align: middle;'
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            layout: 'hbox',
                            defaults: {
                                margin: '5 0 5 5'
                            },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    itemId: 'searchCheckbox',
                                    boxLabel: 'Поиск',
                                    inputValue: 'SEARCH_AGENT',
                                    style: 'vertical-align: middle;',
                                    width: 120
                                },{
                                    itemId: 'searchPort',
                                    xtype: 'textfield',
                                    value: 8889,
                                    width: 70,
                                    style: 'vertical-align: middle;'
                                },{
                                    xtype: 'button',
                                    text: 'Проверить',
                                    handler: function(btn) {
                                        var window = btn.up('window');
                                        window.down('#searchStatus').setSrc('app/resources/images/loading.gif');
                                        window.checkPort(window.down('#serverIpFormField').getValue(), 'SEARCH_AGENT', window.down('#searchPort').getValue(), 'searchStatus');
                                    }
                                }, {
                                    itemId: 'searchStatus',
                                    xtype: 'image',
                                    style: 'vertical-align: middle;'
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            layout: 'hbox',
                            defaults: {
                                margin: '5 0 5 5'
                            },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    boxLabel: 'Индексация',
                                    itemId: 'indexingCheckbox',
                                    inputValue: 'INDEX_AGENT',
                                    style: 'vertical-align: middle;',
                                    width: 120
                                },{
                                    itemId: 'indexingPort',
                                    xtype: 'textfield',
                                    value: 8889,
                                    width: 70,
                                    style: 'vertical-align: middle;'
                                },{
                                    xtype: 'button',
                                    text: 'Проверить',
                                    handler: function(btn) {
                                        var window = btn.up('window');
                                        window.down('#indexingStatus').setSrc('app/resources/images/loading.gif');
                                        window.checkPort(window.down('#serverIpFormField').getValue(), 'INDEX_AGENT', window.down('#indexingPort').getValue(), 'indexingStatus');
                                    }
                                }, {
                                    itemId: 'indexingStatus',
                                    xtype: 'image',
                                    style: 'vertical-align: middle;'
                                }
                            ]
                        }
                    ]
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
                    text:'Добавить',
                    handler: function(btn){
                        var window = btn.up('window');
                        if(window.down('#serverNameFormField').isValid() && window.down('#serverIpFormField').isValid()){

                            var server = window.createServer(window.down('#serverNameFormField').getValue(), window.down('#serverIpFormField').getValue());
                            var coordinatorAdminProcess = window.down('#coordinatorCheckbox').getValue() ? window.createAdminProcess('COORDINATOR', window.down('#coordinatorPort').getValue(), server) : null;
                            var indexingAdminProcess =  window.down('#indexingCheckbox').getValue() ? window.createAdminProcess('INDEX_AGENT', window.down('#indexingPort').getValue(), server) : null;
                            var searchAdminProcess = window.down('#searchCheckbox').getValue() ? window.createAdminProcess('SEARCH_AGENT', window.down('#searchPort').getValue(), server) : null;


                            var adminProcesses = [coordinatorAdminProcess, indexingAdminProcess, searchAdminProcess];
                            var finalAdminProcesses = [];
                            for(var i = 0; i < adminProcesses.length; i++)
                            if(adminProcesses[i] != null) {
                                finalAdminProcesses.push(adminProcesses[i]);
                            };

                            sphinx-console.app.fireEvent('addAdminProcesses', finalAdminProcesses, server);
                            window.close();
                        }
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
    listeners : {
        'show': function(me, eOpts){
            if(me.record){
                this.setTitle('Редактировать сервер');
                Ext.getCmp('serverIdFormField').setValue(me.record.data.id);
                var serverNameFromField = Ext.getCmp('serverNameFormField');
                serverNameFromField.setValue(me.record.data.name);serverNameFromField.prevValue = me.record.data.name;
                var serverIpFormField = Ext.getCmp('serverIpFormField');
                serverIpFormField.setValue(me.record.data.ip);serverIpFormField.prevValue = me.record.data.ip;
                me.down('[itemId=addButton]').setText('Сохранить');
            }else{
                this.setTitle('Добавить сервер');
                Ext.getCmp('serverIdFormField').setValue(null);
                var serverNameFromField = Ext.getCmp('serverNameFormField');
                serverNameFromField.setValue(null);serverNameFromField.prevValue = null;
                var serverIpFormField = Ext.getCmp('serverIpFormField');
                serverIpFormField.setValue(null);serverIpFormField.prevValue = null;
                me.down('[itemId=addButton]').setText('Добавить');
            }
        }
    },
    setRecord: function(record){
      this.record=record;
      return this;
    }
});
