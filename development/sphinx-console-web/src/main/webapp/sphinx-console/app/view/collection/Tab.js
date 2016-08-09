Ext.define('sphinx-console.view.collection.Tab' , {
    extend: 'sphinx-console.view.Tab',
    itemId: 'collectionTab',
    alias: 'widget.collectionTab',
    requires: ['sphinx-console.view.collection.List', 'sphinx-console.view.collection.Search'],
    items: [
        {
            region: 'north',
            xtype:  'collectionSearch'
          },{
            region: 'center',
            xtype:  'collectionList'
        }
    ]
});