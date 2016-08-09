Ext.define('sphinx-console.view.snippet.Search', {
    extend: 'Ext.form.Panel',
    alias: 'widget.snippetSearch',
    id: 'snippetSearchPanel',
    th: this,
    items:[
        {
            xtype : 'fieldset',
            title : 'Параметры поиска',
            layout: 'container',
            defaults: {
                labelWidth: 150,
                width: '50%',
                margin: 5
            },
            items: [
                {
                    xtype: 'textfield',
                    itemId: 'collectionNameField',
                    name: 'collectionName',
                    fieldLabel: 'Коллекция',
                    emptyText: 'Введите полностью или часть наименования коллекции'
                },
                {
                    xtype: 'button',
                    text : 'Найти',
                    cls : 'searchbutton',
                    handler: function(){
                        Ext.getCmp('snippetSearchPanel').findSnippets();
                    }
                },
                {
                    xtype: 'button',
                    text : 'Очистить фильтр',
                    cls : 'clearfilterbutton',
                    handler: function(){
                        Ext.getCmp('snippetSearchPanel').down('#collectionNameField').setValue('');
                        Ext.getCmp('snippetSearchPanel').findSnippets();
                    }
                }
            ]
        }
    ],

    findSnippets: function(){
        var store = Ext.getStore('SnippetData');
        var filterCollectionName = this.down('#collectionNameField').getValue();
        store.proxy.setExtraParam('collectionName', filterCollectionName);
        store.load();
    }

});