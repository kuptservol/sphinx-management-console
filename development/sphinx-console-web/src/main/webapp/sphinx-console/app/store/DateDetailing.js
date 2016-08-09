Ext.define('sphinx-console.store.DateDetailing', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.ValueTitle',
    autoLoad: false,
    autoSync: false,
    proxy: {
        type: 'ajax',
        format: 'json',
        actionMethods:  {read: "GET"},
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/dateDetailings'
    }
});