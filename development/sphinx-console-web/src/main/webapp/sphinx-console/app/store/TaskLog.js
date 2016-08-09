Ext.define('sphinx-console.store.TaskLog', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.TaskLog',
    timeout:7000,
    autoLoad: false,
    pageSize: 10,
    remoteSort: true,
    remoteFilter: true,
    buffered: true,
    listeners: {
        load:function(el,records){
        	
        },
        beforeload: function(store, operation,eOpts) {

        }
    },
    proxy: {
        type: 'jsonajax',
        format: 'json',
        paramsAsJson : true,
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/taskLog',
        
        reader: {             
        	type: 'json',
        	rootProperty: 'list',
        	totalProperty: 'total'
        } 

    }
    
});







