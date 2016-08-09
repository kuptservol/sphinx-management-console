Ext.define('sphinx-console.view.servers.Search', {
    extend: 'Ext.form.Panel',
    alias: 'widget.serversSearch',
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
                    id : 'serverSearchField',
                    fieldLabel: 'Сервер',
                    xtype : 'textfield',
                    emptyText : 'Введите полностью или часть имени сервера'
                },
                {
                    xtype: 'button',
                    text : 'Найти',
                    cls : 'searchbutton',
                    handler: function(){
                        sphinx-console.app.getsphinx-consoleControllerServersController().loadPage(1,Ext.getCmp('serverSearchField').getValue());
                    }
                },
                {
                    xtype: 'button',
                    text : 'Очистить фильтр',
                    cls : 'clearfilterbutton',
                    handler: function(){
                        Ext.getCmp('serverSearchField').setValue('');
                        sphinx-console.app.getsphinx-consoleControllerServersController().loadPage(1,null);
                    }
                }
            ]
        }
    ]
});