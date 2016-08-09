Ext.define('sphinx-console.view.adminProcess.AdminProcess', {
    extend: 'Ext.window.Window',
    title : 'Добавить процесс',
    width : 720,
    height: 240,
    closable: false,
    closeAction: 'hide',
    items : [
        {
            xtype: 'panel',
            title: 'Укажите параметры процесса',
            layout: {
                type: 'table',
                columns: 3
            },
            height: 156,
            items: [
                {
                    xtype: 'hiddenfield',
                    id: 'adminProcessIdFormField',
                    colspan: 1
                },
                {
                    fieldLabel: 'Тип <font color="red">*</font>',
                    labelWidth: '30px',
                    xtype : 'combobox',
                    store: 'ProcessType',
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'type',
                    colspan: 1
                },
                {
                    fieldLabel: 'Порт <font color="red">*</font>',
                    labelWidth: '30px',
                    xtype : 'textfield',
                    emptyText : 'Введите порт процесса',
                    padding: '3 10 3 3',
                    allowBlank: false,
                    blankText: 'Поле обязательно для заполнения',
                    validFlag : true,
                    prevValue: '',
                    colspan: 1,
                    validator : function() {
                        return this.validFlag;
                    },
                    listeners : {
                    }
                },
                {
                    xtype: 'button',
                    text: 'Тестировать соединение',
                    colspan: 1,
                    handler:function(){
                        var labelStatus = this.up('window').down('[itemId=connectionLabel]');
                        labelStatus.setHtml('<font color="#87cefa">Установка соединения...</font>');
                        labelStatus.show();
                        Ext.Ajax.request({
                            url: sphinx-console.util.Utilities.SERVER_URL + '/view/adminProcessStatus',
                            method: 'POST',
                            useDefaultXhrHeader: false,
                            headers: { 'Content-Type': 'application/json' },
                            jsonData: {
                                "server":{"ip": this.up('window').recordServer.data.ip},
                                "type":this.up('panel').down('[xtype=combobox]').getValue(),
                                "port":this.up('panel').down('[xtype=textfield]').getValue()
                            },
                            method: 'POST',
                            success: function(response, opts) {
                                switch(response.responseText){
                                   case '"RUNNING"' : labelStatus.setHtml('<font color="green">Соединение установлено</font>');break;
                                   case '"STOPPED"' : labelStatus.setHtml('<font color="red">Соединение не установлено</font>');break;
                                }
                            }
                        });
                    }
                },
                {
                    xtype: 'label',
                    itemId: 'connectionLabel',
                    text: '',
                    colspan: 4
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
                    xtype: 'button',
                    text: 'Добавить',
                    handler: function(){
                        var server = new Object();
                        server.ip = this.up('window').recordServer.data.ip;
                        server.name = this.up('window').recordServer.data.name;
                            type=this.up('panel').down('[xtype=combobox]').getValue(),
                            port=this.up('panel').down('[xtype=textfield]').getValue();
                        if(this.up('window').recordAdminProcess){
                            sphinx-console.app.fireEvent('updateAdminProcess',type,port,server,this.up('window').recordAdminProcess.data.id);
                        }else{
                            var adminProcess = new Object();
                            adminProcess.server = server;
                            adminProcess.type = type;
                            adminProcess.port = port;
                            sphinx-console.app.fireEvent('addAdminProcess',adminProcess);
                        }
                        this.up('window').hide();
                    }
                },{
                    xtype: 'button',
                    text: 'Отмена',
                    handler:function(){
                        this.up('window').hide();
                    }
                }
            ]
        }
    ],
    listeners : {
        'show': function(me, eOpts){
            me.down('label').setHtml('');
            if(me.recordAdminProcess){
                this.setTitle('Редактировать процесс');
                var type = this.down('[xtype=combobox]');
                type.setValue(me.recordAdminProcess.data.type);type.prevValue=me.recordAdminProcess.data.type;
                var port = this.down('[xtype=textfield]');
                port.setValue(me.recordAdminProcess.data.port);port.prevValue=me.recordAdminProcess.data.port;
                this.down('toolbar').down('button').setText('Изменить');
            }else{
                this.setTitle('Добавить процесс');
                var type = this.down('[xtype=combobox]');
                type.setValue(null);type.prevValue=null;
                var port = this.down('[xtype=textfield]');
                port.setValue(null);port.prevValue=null;
                this.down('toolbar').down('button').setText('Добавить');
            }
        }
    },
    setRecordServer: function(record){
        this.recordServer=record;
    },
    setRecordAdminProcess: function(record){
        this.recordAdminProcess=record;
    }
});
