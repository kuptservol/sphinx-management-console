Ext.define('sphinx-console.view.collection.Search', {
    extend: 'Ext.form.Panel',
    alias: 'widget.collectionSearch',
    requires: 'sphinx-console.model.Server',
    layout: {
        type: 'fit'
    },
    items:[{
        xtype : 'fieldset',
        title : 'Параметры поиска',
        layout: 'container',
        height: '100%',
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
                xtype: 'combo',
                itemId: 'indexServerField',
                name: 'indexServerField',
                fieldLabel: 'Сервер индексации',
                store: Ext.create('sphinx-console.store.TypedServersStore',{processType: 'INDEXING'}),
                emptyText: 'Все',
                displayField: 'name',
                editable: false
            },
            {
                xtype: 'combo',
                itemId: 'searchServerField',
                name: 'searchServerField',
                fieldLabel: 'Сервер поиска',
                store: Ext.create('sphinx-console.store.TypedServersStore',{processType: 'SEARCHING'}),
                emptyText: 'Все',
                displayField: 'name',
                editable: false
            },
            {
                xtype: 'container',
                layout: {type: 'hbox', align: 'end'},
                background: 'red',
                items: [
                    {
                        itemId: 'searchCollections',
                        xtype: 'button',
                        text: 'Найти',
                        cls : 'searchbutton',
                        handler: function(searchButton) {
                            var collectionName = searchButton.up('form').getForm().findField('collectionName').getSubmitValue();
                            var indexServer = searchButton.up('form').getForm().findField('searchServerField').getSubmitValue();
                            var searchServer = searchButton.up('form').getForm().findField('indexServerField').getSubmitValue();
                            sphinx-console.app.fireEvent('searchCollections',collectionName, indexServer, searchServer);
                        }
                    },
                    {
                        itemId: 'clearFilterCollections',
                        xtype: 'button',
                        cls : 'clearfilterbutton',
                        text: 'Очистить фильтр',
                        handler: function(searchButton) {
                            searchButton.up('form').getForm().reset();
                        }
                    }
                ]
            }]
    }]
});