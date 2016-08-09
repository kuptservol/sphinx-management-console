Ext.define('sphinx-console.store.ProcessType', {
    extend: 'Ext.data.Store',
    fields: ['type', 'name'],
    data : [
        {"type":"COORDINATOR", "name":"Координатор"},
        {"type":"SEARCH_AGENT", "name":"Агент поиска"},
        {"type":"INDEX_AGENT", "name":"Агент индексации"}
    ]
});
