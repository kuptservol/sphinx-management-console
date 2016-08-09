Ext.define('sphinx-console.view.adminProcess.Window', {
    extend: 'Ext.window.Window',
    title : 'Процессы',
    width : 740,
    height: 400,
    closable: false,
    closeAction: 'hide',
    requires: ['sphinx-console.view.adminProcess.List'],
    items : [
        {
          xtype: 'fieldset',
          height: "100%",
          items:[
              {
                  xtype: 'panel',
                  height: 333,
                  bodyPadding: 1,
                  layout: 'fit',    // Specifies that the items will now be arranged in columns
                  fieldDefaults: {
                      labelAlign: 'left',
                      msgTarget: 'side'
                  },
                  dockedItems: [{
                      xtype: 'toolbar',
                      items: [
                          {
                              xtype:'panel',
                              width: "100%",
                              items:[
                                  {
                                      xtype : 'label',
                                      itemId: 'serverIpFormLabel'
                                  },{
                                      xtype: 'fieldset',
                                      items:[
                                          {
                                              xtype: 'button',
                                              text: 'Обновить',
                                              handler: function(){
                                                  sphinx-console.app.fireEvent("refreshAdminProcesses");
                                              }
                                          },
                                          {
                                              xtype: 'button',
                                              text: 'Добавить процесс',
                                              handler: function(){
                                                  sphinx-console.app.fireEvent('addAdminProcessPopup',this.up('window').recordServer);
                                              }
                                          }
                                      ]
                                  }
                              ]
                          }
                      ]
                  }],
                  items: [
                      {
                          xtype: 'adminProcessList',
                          columnWidth: .100,
                          height: "90%"
                      }
                  ]
              }
          ]
        }
    ],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        items:[
            {
                xtype: 'button',
                text: 'Закрыть',
                handler:function(){
                    this.up('window').close();
                }
            }
        ]
    }],
    listeners : {
        'show': function(me, eOpts){
            if(me.recordServer){
                this.setTitle('Сервер '+me.recordServer.data.name+". Процессы");
                me.down("label[itemId=serverIpFormLabel]").setText('Адрес сервера:'+me.recordServer.data.ip);
                sphinx-console.app.fireEvent("adminProcessesPopup",me.recordServer.data.id);
            }
        },
        'hide': function(me){
            sphinx-console.app.fireEvent('hideAdminProcessPopupList');
        }
    },
    setRecordServer: function(record){
        this.recordServer=record;
    },
    getRecordServer: function(){
        return this.recordServer;
    }
});

