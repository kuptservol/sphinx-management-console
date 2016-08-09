Ext.define('sphinx-console.store.TaskNames', {
    extend: 'Ext.data.Store',
    idProperty: 'title',
    fields: [
        {
            name: 'taskName'
        },
        {
            name: 'taskNameTitle'
        }
    ],
    autoLoad: true,
    autoSync: false,
    proxy: {
        type: 'rest',
        format: 'json',
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/taskNames'
    }
});
