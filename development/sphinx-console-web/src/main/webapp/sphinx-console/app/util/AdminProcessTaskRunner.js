Ext.define('sphinx-console.util.AdminProcessTaskRunner', {
    extend: 'Ext.util.TaskRunner',

    startUpdateInfo: function(adminProcesses) {
       // this.start(this.getStatusTask(adminProcesses));
    },
    getStatusTask: function(adminProcesses) {
        var run = function () {
            sphinx-console.app.getsphinx-consoleControllerServersController().fireEvent('updateAdminProcessStatusTasks', adminProcesses);
        };

        var task = {
            run: run,
            interval: sphinx-console.util.Utilities.SERVER_TASK_INTERVAL
        };
        return task;
    }
});