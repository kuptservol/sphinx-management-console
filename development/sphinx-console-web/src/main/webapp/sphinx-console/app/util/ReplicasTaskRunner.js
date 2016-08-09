Ext.define('sphinx-console.util.ReplicasTaskRunner', {
    extend: 'Ext.util.TaskRunner',

    startTask: function() {
        this.start(this.updateReplicasTask());
    },

    updateReplicasTask: function() {
        var run = function () {
            sphinx-console.app.fireEvent('updateReplicas', false);
        };

        var task = {
            run: run,
            interval: sphinx-console.util.Utilities.SERVER_TASK_INTERVAL
        };
        return task;
    }
});