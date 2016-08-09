Ext.define('sphinx-console.view.searchQuery.Tab', {
    itemId: 'searchQueryTabId',
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.searchQueryTab',
    requires: ['sphinx-console.view.searchQuery.List', 'sphinx-console.view.searchQuery.Search'],
    listeners: {
        activate: function () {
            if(sphinx-console.util.Utilities.QUERY_LOG_TAB_ENABLED) {
                Ext.getCmp('searchQuerySearchPanel').down('#searchQueryCollectionNameCombo').getStore().reload();
                Ext.getCmp('searchQuerySearchPanel').findSearchQueries();
            }
        }
    },

    initComponent: function(){
        var th = this;
        if(sphinx-console.util.Utilities.QUERY_LOG_TAB_ENABLED){
            this.items = [
                {
                    region: 'north',
                    xtype: 'searchQuerySearch'
                },
                {
                    region: 'center',
                    xtype: 'searchQueryList'
                }
            ];
        } else {
            this.items = [
                {
                    xtype: 'label',
                    text: 'Сбор статистики выключен, см. конфигурацию приложения.',
                    margin: 5,
                    border: 0
                }
            ];

        }
        this.callParent(arguments);
    }
});
