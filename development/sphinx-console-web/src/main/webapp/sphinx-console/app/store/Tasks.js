Ext.define('sphinx-console.store.Tasks', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.Task',
    timeout:120000,
   // autoLoad: {start: 0, limit: pageSize},
    autoLoad: false,
    autoSync: false,
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
        timeout: 120000,
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/tasks',
        
        reader: {             
        	type: 'json',
        	rootProperty: 'list',
        	totalProperty: 'total'
        } 

    }
    
});





Ext.define('Ext.ux.data.proxy.JsonAjaxProxy', {
	extend:'Ext.data.proxy.Rest',
	alias:'proxy.jsonajax',

	actionMethods : {
	    create: "POST",
	    read: "POST",
	    update: "POST",
	    destroy: "POST"
	},

	buildRequest:function (operation) {
	    var request = this.callParent(arguments);
           
	        // For documentation on jsonData see Ext.Ajax.request
	        request.jsonData = request.params;
	        request.params = {};

	        return request;
	},

	/*
	 * @override
	 * Inherit docs. We don't apply any encoding here because
	 * all of the direct requests go out as jsonData
	 */
	applyEncoding: function(value){
		return value;
	}

	});



