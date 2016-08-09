Ext.define('sphinx-console.view.logs.ErrorDetailWindow', {
    extend : 'Ext.window.Window',
    width: 900,
    height: 650,
    layout: 'fit',
    maximizable: false,
    autoScroll:true,
    modal: true,
    title: 'Описание ошибки',
    bodyStyle:"padding:10 10 0 10px;background:#FFFFFF;background-color:#FFFFFF",

    buttons: [{
        text: 'Закрыть',
        handler: function(btn) {
            btn.up('window').close();
        }
    }]
});