Ext.define('sphinx-console.view.collection.wizard.SqlErrorWindow', {
    extend: 'Ext.window.Window',
    title : 'Проверка SQL',
    modal: true,
    width: 700,
    height: 300,
    closeAction: 'destroy',
    configuration: null,

    constructor: function (configuration) {
        if(configuration) this.configuration = configuration;
        this.superclass.constructor.call(this);
    },

    initComponent: function() {
        this.items = [
            {
                xtype: 'panel',
                layout: 'vbox',
                margin: 5,
                width: '100%',
                items: [
                    {
                        itemId: 'fieldType',
                        xtype : 'label',
                        style: 'font-weight: bold;',
                        margin: '10 0 0 0'
                    },
                    {
                        xtype : 'label',
                        text: 'Передан некорректный SQL запрос',
                        margin: '10 0 0 0'

                    },
                    {
                        itemId: 'sqlError',
                        xtype : 'label',
                        width: 680,
                        margin: '10 5 0 0'
                    }
                ]
            }
        ];
        this.callParent(arguments);
        if(this.configuration) {
            this.down('#fieldType').setText(this.configuration.fieldType);
            this.down('#sqlError').setText(this.configuration.sqlError);
        }
    },
    fbar: [
        {
            type: 'button',
            text: 'ОК',
            handler: function (btn) {
                btn.up('window').close();
            }
        }
    ]
});
