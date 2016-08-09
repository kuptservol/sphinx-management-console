Ext.define('sphinx-console.store.Servers', {
    extend: 'Ext.data.Store',
    requires: ['sphinx-console.store.SyncProxy'],
    autoLoad: false,
    autoSync: false,
    autoDestroy: false,
    remoteFilter: true,
    timeout:1000,
    fields:[{name: 'id'},{name: "ip"},{name: 'name'},{name: 'status'}],
    listeners: {
        load:function(el,records){
        }
    },
    proxy: {
        type: 'syncProxy',
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/servers',
        format: 'json',
        reader: {
            type: 'json',
            rootProperty: 'list',
            totalProperty: 'total'
        },
        paramsAsJson : true,
        actionMethods:  {create: "POST", read: "POST", update: "POST", destroy: "DELETE"},
        api: {
            create: sphinx-console.util.Utilities.SERVER_URL + '/configuration/server/add',
            update: sphinx-console.util.Utilities.SERVER_URL + '/configuration/server/add',
            read: sphinx-console.util.Utilities.SERVER_URL + '/view/servers',
            destroy:sphinx-console.util.Utilities.SERVER_URL + '/configuration/server/delete'
        }
    },
    filters: [
        Ext.create('Ext.util.Filter', {id: "serverName", property: "serverName", anyMatch: true, value: ""})]
});
