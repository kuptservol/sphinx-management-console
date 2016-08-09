Ext.define('sphinx-console.view.servers.EditServerWindow', {
    extend: 'Ext.window.Window',
    title : 'Добавить сервер',
    closable: false,
    closeAction: 'destroy',
    layout: {type: 'vbox', align: 'stretch'},
    modal: true,
    items : [
            {
                xtype: 'hiddenfield',
                id: 'serverIdFormField'
            },
            {
                fieldLabel: 'Имя <font color="red">*</font>',
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
                xtype : 'textfield',
                id: 'serverIpFormField',
                emptyText : 'Введите IP-адрес сервера',
                padding: '3 10 3 3',
                allowBlank: false,
                blankText: 'Поле обязательно для заполнения',
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
                    text:'Редактировать',
                    handler: function(){
                        if(Ext.getCmp('serverNameFormField').isValid()&&Ext.getCmp('serverIpFormField').isValid()){
                            sphinx-console.app.fireEvent('updateServer',
                                Ext.getCmp('serverIdFormField').getValue(),
                                Ext.getCmp('serverNameFormField').getValue(),
                                Ext.getCmp('serverIpFormField').getValue()
                            );
                            this.up('window').close();
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
            }
        }
    },
    setRecord: function(record){
      this.record = record;
      return this;
    }
});
