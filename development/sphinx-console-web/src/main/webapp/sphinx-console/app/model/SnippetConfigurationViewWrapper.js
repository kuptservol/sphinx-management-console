Ext.define('sphinx-console.model.SnippetConfigurationViewWrapper', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'collectionName', type: 'string'},
        {name: 'cronSchedule', type: 'string'},
        {name: 'lastBuildSnippet', type: 'date', dateFormat: 'time'},
        {name: 'lastBuildSnippetString',
            calculate: function (data) {
                return data.lastBuildSnippet ? Ext.Date.format(data.lastBuildSnippet, 'd.m.Y H:i:s') : null;
            }
        },
        {name: 'nextBuildSnippet', type: 'date', dateFormat: 'time'},
        {name: 'isCurrentlyRebuildSnippet', type: 'boolean'},
        {name: 'snippetInfoWrapper'},
        {
            name: 'isCurrentlyRebuildSnippet',
            calculate: function (data) {
                return data.snippetInfoWrapper ? data.snippetInfoWrapper.isCurrentlyRebuildSnippet : false;
            }
        },
        {
            name: 'isCurrentlyFullRebuildSnippet',
            calculate: function (data) {
                return data.snippetInfoWrapper ? data.snippetInfoWrapper.isCurrentlyFullRebuildSnippet : false;
            }
        },
        {
            name: 'isSheduleEnabled',
            calculate: function (data) {
                return data.snippetInfoWrapper ? data.snippetInfoWrapper.isSheduleEnabled : false;
            }
        }
    ]
});
