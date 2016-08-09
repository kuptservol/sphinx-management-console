Ext.define('sphinx-console.view.servers.ServerViewForm', {
    extend: 'Ext.window.Window',
    title : 'Сервер',
    closable: true,
    modal:true,
    closeAction: 'destroy',
    layout: 'vbox',
    default: {
        labelWidth: 20
    },
    server: null,
    constructor: function(server) {
        this.server = server;
        this.superclass.constructor.call(this);
    },
    initComponent: function() {
        this.items = [
            {
                fieldLabel: 'Имя',
                labelWidth: '20px',
                xtype : 'displayfield',
                id: 'serverName',
                name: 'serverName',
                padding: '3 10 3 3',
                value: this.server.name
            },
            {
                fieldLabel: 'IP-адрес',
                labelWidth: '20px',
                xtype : 'displayfield',
                id: 'serverIp',
                name: 'serverIp',
                padding: '3 10 3 3',
                value: this.server.ip
            }
        ];
        this.callParent(arguments);
    }
});
