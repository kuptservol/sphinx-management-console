Ext.define('sphinx-console.store.AdminProcess', {
    extend: 'Ext.data.Store',
    autoLoad: false,
    autoSync: false,
    remoteFilter: true,
    pageSize: 10,
    fields:[
            {
                name: 'idAdminProcess',
                calculate: function (data) {
                    return data.id;
                }
            },
            {
                name: "port"
            },
            {
                name: 'portAgent',
                calculate: function (data) {
                    return data.port;
                }
            },
            {
                name: "server"
            },
            {
                name: 'idServer',
                calculate: function (data) {
                    return data.server.id;
                }
            },
            {
                name: 'serverAddress',
                calculate: function (data) {
                    return data.server.ip;
                }
            },
            {
                name: 'serverName',
                calculate: function (data) {
                    return data.server.name;
                }
            },
            {
                name: "type"
            },
            {
                name: 'coordinator',
                calculate: function(data){
                    return data.type;
                }
            },
            {
                name: 'status',
                calculate: function(data){
                    return '';
                }
            }
       ],
    listeners: {
        load:function(el,records){
        }
    },
    proxy: {
        type: 'jsonajax',
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/adminProcesses',
        format: 'json',
        reader: {
            type: 'json',
            rootProperty: 'list',
            totalProperty: 'total'
        },
        paramsAsJson : true,
        api: {
            create: sphinx-console.util.Utilities.SERVER_URL + '/configuration/adminProcess/add',
            update: sphinx-console.util.Utilities.SERVER_URL + '/configuration/adminProcess/update',
            read: sphinx-console.util.Utilities.SERVER_URL + '/view/adminProcesses',
            remove:sphinx-console.util.Utilities.SERVER_URL + '/configuration/adminProcess/delete'
        }
    },
    filters: [
        Ext.create('Ext.util.Filter', {id: "serverId", property: "serverId", anyMatch: true, value: ""})]
});