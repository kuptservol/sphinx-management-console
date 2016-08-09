Ext.define('sphinx-console.view.snippet.wizard.Step4Snippet' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.step4Snippet',
    title: 'Полный пересбор сниппетов',
    trackResetOnLoad: true,
    defaults: { // defaults are applied to items, not the container
        labelSeparator: '',
        width: '100%',
        msgTarget: 'under'
    },
    bodyPadding: 20,

/*    listeners: {
        validitychange: function(form, valid, eOpts){
            this.up('snippetWizard').fireEvent('activeFormValidityChange');
        }
    },
*/

    initComponent: function(){
        this.items = [
            {
                xtype: 'panel',
                layout: 'vbox',
                items: [
                    {
                        xtype: 'label',
                        text: 'Все запросы будут выполняться к датасорсу, указанному для индексации выбранной коллекции',
                    },
                    {
                        xtype: 'textAreaWithNote',
                        itemId: 'fullRebuildPreQuery',
                        labelText: 'Предзапрос:',
                        noteText: 'Предзапрос может состоять из нескольких SQL-выражений, разделенных символом ; (точка с запятой)'
                    },
                    {
                        xtype: 'textAreaWithNote',
                        itemId: 'fullRebuildMainQuery',
                        labelText: 'Основной запрос:',
                        noteText: 'Запрос должен возвращать первым полем id записи в поиске, а затем - поля, по которым должны формироваться сниппеты'
                    },
                    {
                        xtype: 'textAreaWithNote',
                        itemId: 'fullRebuildPostQuery',
                        labelText: 'Постзапрос:',
                        noteText: 'Постзапрос может состоять из нескольких SQL-выражений, разделенных символом ; (точка с запятой)'
                    }
                ]
            }
        ];
        this.callParent(arguments);

    },

    getData : function(){
        var fullRebuildQueries = new Object();
        fullRebuildQueries.fullRebuildPreQuery = this.down('#fullRebuildPreQuery').down('textarea').getValue();
        fullRebuildQueries.fullRebuildMainQuery = this.down('#fullRebuildMainQuery').down('textarea').getValue();
        fullRebuildQueries.fullRebuildPostQuery = this.down('#fullRebuildPostQuery').down('textarea').getValue();
        return fullRebuildQueries;
    },

    loadData: function(queries) {
        this.down('#fullRebuildPreQuery').down('textarea').setValue(queries.fullRebuildPreQuery);
        this.down('#fullRebuildMainQuery').down('textarea').setValue(queries.fullRebuildMainQuery);
        this.down('#fullRebuildPostQuery').down('textarea').setValue(queries.fullRebuildPostQuery);
    }

});