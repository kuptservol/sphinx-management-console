Ext.define('sphinx-console.util.CollectionTaskRunner', {
    extend: 'Ext.util.TaskRunner',

    getCollectionSize: function(collectionName) {
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/collectionSize/' + collectionName,
            method: 'GET',
            success: function(response) {
                sphinx-console.app.fireEvent('updateCollectionSize', collectionName, response.responseText);
            }
        });
    },

    getSearchStatus: function(collectionName) {
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/process/' + collectionName + '/status',
            method: 'GET',
            success: function(response) {
                var result = (response.responseText == '"SUCCESS"');
                sphinx-console.app.fireEvent('updateCollectionSearchStatus',collectionName, result);
            },
            failure: function(error) {
                sphinx-console.app.fireEvent('updateCollectionSearchStatus',collectionName, false);
            }
        });
    },

    getIndexStatus: function(collectionName) {
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/currentlyIndexing/' + collectionName + '/INDEXING',
            method: 'POST',
            success: function(response) {
                var result = Ext.JSON.decode(response.responseText).result;
                sphinx-console.app.fireEvent('updateCollectionIndexStatus',collectionName, result);
            },
            failure: function(error) {
                sphinx-console.app.fireEvent('updateCollectionIndexStatus',collectionName, false);
            }
        });
    },

    getCollectionInfo:  function() {
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/queryCollectionsInfo',
            method: 'GET',
            success: function(response) {
                var result = Ext.JSON.decode(response.responseText);
                sphinx-console.app.fireEvent('updateCollectionsInfo', result);
            },
            failure: function(error) {
                sphinx-console.app.fireEvent('updateCollectionsInfo', false);
            }
        });
    },

    startTask: function() {
        this.start(this.createCollectionInfoTask());
    },

    createCollectionInfoTask: function() {
        var self = this;
        var run = function () {
            self.getCollectionInfo();
        };

        var task = {
            run: run,
            interval: sphinx-console.util.Utilities.SERVER_TASK_INTERVAL
        };
        return task;
    },

    createCollectionSizeTask: function(collectionName) {
        var self = this;
        var run = function () {
            self.getCollectionSize(collectionName);
        };

        var task = {
            run: run,
            interval: sphinx-console.util.Utilities.SERVER_TASK_INTERVAL
        };
        return task;
    },

    createCollectionSearchStatusTask: function(collectionName) {
        var self = this;
        var run = function () {
            self.getSearchStatus(collectionName);
        };

        var task = {
            run: run,
            interval: sphinx-console.util.Utilities.SERVER_TASK_INTERVAL
        };

        return task;
    },

    createCollectionIndexStatusTask: function(collectionName) {
        var self = this;
        var run = function () {
            self.getIndexStatus(collectionName);
        };

        var task = {
            run: run,
            interval: sphinx-console.util.Utilities.SERVER_TASK_INTERVAL
        };

        return task;
    }
});