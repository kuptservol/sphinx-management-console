Ext.define('sphinx-console.view.TextAreaWithNote' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.textAreaWithNote',
    layout: 'hbox',
    width: '100%',
    margin: '20 0 0 0',
    labelText: null,
    textAreaHeight: 120,
    noteText: null,

    initComponent: function(){
        var th = this;
        this.items = [
            {
                xtype: 'label',
                text: th.labelText,
                flex: 1
            },
            {
                xtype: 'panel',
                layout: 'vbox',
                flex: 6,
                items: [
                    {   xtype: 'textarea',
                        height: th.textAreaHeight,
                        autoScroll: true,
                        width: '100%',
                        enableKeyEvents: true,
                        fieldStyle: {
                            display: 'inherit'
                        }
                    },
                    {
                        xtype: 'label',
                        text: th.noteText,
                        cls: 'note-label',
                        margin: '-10 0 0 0'
                    }
                ]
            }
        ];
        this.callParent(arguments);

    }
});