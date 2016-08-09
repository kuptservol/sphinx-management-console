Ext.define('sphinx-console.store.FieldMapping', {
    extend: 'Ext.data.Store', //Ext.data.ArrayStore
    autoLoad: false,
    autoSync: false,
    model: 'sphinx-console.model.FieldMapping',
    sorters: 'id',
    sortOnLoad: true,
    listeners: {
        load:function(el,records){
        }
    }
   // data:  [[1, 'id', 'id', '1', 'Нет', '', true], [2, 'id2', 'id2', '1', 'Нет', '', false], [3, 'id3', 'id3', '1', 'sql_attr_bool', '', false]]
    
   /* data: [ { category: 'Action', name: 'Django Unchained' },  
                                { category: 'Comedy', name: 'Hangover' }, 
                                { category: 'Drama', name: '50/50' },  
                                 { category: 'Other', name: 'Old Boy' } ], */

});
