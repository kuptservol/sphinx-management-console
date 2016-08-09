Ext.define('sphinx-console.model.Task', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    fields: [
        {name: 'taskUid', type: 'string'},
        {name: 'taskName', type: 'string'},
        {name: 'collectionName', type: 'string'},
        {name: 'startTime', type: 'date', dateFormat: 'time'},
        {name: 'endTime', type: 'date', dateFormat: 'time'},
        {name: 'status', type: 'string'},
        {name: 'stage', type: 'string'}]
});