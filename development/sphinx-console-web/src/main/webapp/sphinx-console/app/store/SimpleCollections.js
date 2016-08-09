Ext.define('sphinx-console.store.SimpleCollections', {
    extend: 'Ext.data.Store',
    model: 'sphinx-console.model.SimpleCollectionWrapper',
/*    listeners: {
        load:function(el,records){
            alert('Load SimpleCollections store')
        }
    },*/
    proxy: {
        type: 'ajax',
        format: 'json',
        paramsAsJson : true,
        url: sphinx-console.util.Utilities.SERVER_URL + '/view/simpleCollections',
        reader: {
            type: 'json'
        },
        actionMethods: {
            read: 'GET'
        }
    }
});
