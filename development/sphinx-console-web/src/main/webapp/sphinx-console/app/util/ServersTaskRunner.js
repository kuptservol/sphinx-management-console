Ext.define('sphinx-console.util.ServersTaskRunner', {
    extend: 'Ext.util.TaskRunner',

    startUpdateInfo: function(servers) {
        this.start(this.getServersInfo(servers));
    },
    getStatusTask: function(servers) {
        var run = function () {
            //sphinx-console.app.getsphinx-consoleControllerServersController().fireEvent('updateServerStatusTasks', servers);
            var urlServerStatus = sphinx-console.util.Utilities.SERVER_URL + '/view/serverStatus';
            Ext.Array.each(servers, function(server) {
                Ext.Ajax.request({
                    url: urlServerStatus,
                    method: 'POST',
                    useDefaultXhrHeader: false,
                    headers: { 'Content-Type': 'application/json' },
                    jsonData: {"id": server.data.id},
                    success: function(response, opts) {
                        var status = '';
                        switch(response.responseText){
                            case "\"RUNNING\"" : status = "Запущен";break;
                            case "\"STOPPED\"" : status = "Остановлен";break;
                        }
                        server.data.status=status;
                        server.commit();
                    },
                    error: function(msg){
                        if (sphinx-console.util.Utilities.DEBUG)
                            console.log(msg);
                    }
                });
            });
        };

        var task = {
            run: run,
            interval: sphinx-console.util.Utilities.SERVER_TASK_INTERVAL
        };
        return task;
    },

    getServersInfo: function(servers) {
        var run = function () {
            var result = null;
            Ext.Ajax.request({
                url: sphinx-console.util.Utilities.SERVER_URL + '/view/queryServersInfo',
                method: 'GET',
                useDefaultXhrHeader: false,
                headers: { 'Content-Type': 'application/json'},
                success: function(response, opts) {
                    result = Ext.JSON.decode(response.responseText);
                    Ext.Array.each(servers, function(server) {
                        var status ='';
                        var serverStatus = result[server.get('name')] ? result[server.get('name')].serverStatus : '';
                        switch(serverStatus){
                            case "RUNNING" : status = "Запущен"; break;
                            case "STOPPED" : status = "Остановлен"; break;
                        }
                        server.data.status = status;
                        server.commit();
                    });
                },
                error: function(msg){
                    if (sphinx-console.util.Utilities.DEBUG)
                        console.log(msg);
                }
            });

        };

        var task = {
            run: run,
            interval: sphinx-console.util.Utilities.SERVER_TASK_INTERVAL
        };
        return task;
    }
});