Ext.define('sphinx-console.store.SyncProxy', {
    extend:'sphinx-console.store.sphinx-consoleProxy',
    alias:'proxy.syncProxy',

    actionMethods:  {create: "POST", read: "GET", update: "POST", destroy: "DELETE"},

    afterRequest: function(request, success){
        if(success){
            var response = Ext.JSON.decode(request.getOperation().getResponse().responseText),
                method = request.getMethod(),
                action = request.getAction();
            switch (method){
                case 'POST':
                    if(action=='create'||action=='update'){
                        Ext.Msg.alert('Статус', response.code==0?'Сохранение успешно выполнено!':'Сохранение не может быть выполнено!');
                    }
                    break;
                case 'DELETE':
                    if(action=='destroy'){
                    	Ext.Msg.minWidth = 750;
                    	
                    	var error = response.stackTrace;
                    	
                    	var errorMsg;
                    	
                    	if (error) {
                    		errorMsg = error;
                    	} else {
                    		error = "";
                    	}
                    	
                    	Ext.Msg.alert('Статус', response.code==0?'Удаление успешно выполнено!':'Удаление не может быть выполнено! ' + error);
                    }
                    break;
            }
        }
    },
    listeners: {
        exception: function(proxy, response, options) {
        }
    }

});