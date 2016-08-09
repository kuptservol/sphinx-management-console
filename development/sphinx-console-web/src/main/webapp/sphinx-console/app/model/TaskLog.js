Ext.define('sphinx-console.model.TaskLog', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    fields: [
        {name: 'id', type: 'int'},
        {name: 'taskUid', type: 'string'},
        {name: 'taskName', type: 'string'},
        {name: 'serverName', type: 'string'},
        {name: 'startTime', type: 'date', dateFormat: 'time'},
        {name: 'endTime', type: 'date', dateFormat: 'time'},
        {name: 'status', type: 'string'},
        {name: 'stage', type: 'string'}]
});