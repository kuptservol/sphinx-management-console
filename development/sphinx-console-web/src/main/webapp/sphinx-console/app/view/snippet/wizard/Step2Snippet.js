Ext.define('sphinx-console.view.snippet.wizard.Step2Snippet' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.step2Snippet',
    title: 'Дельта-индексация',
    trackResetOnLoad: true,
    requires: ['sphinx-console.view.TextAreaWithNote'],
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
                        itemId: 'preQuery',
                        labelText: 'Предзапрос:',
                        noteText: 'Предзапрос может состоять из нескольких SQL-выражений, разделенных символом ; (точка с запятой)'
                    },
                    {
                        xtype: 'textAreaWithNote',
                        itemId: 'mainQuery',
                        labelText: 'Основной запрос:',
                        noteText: 'Запрос должен возвращать первым полем id записи в поиске, а затем - поля, по которым должны формироваться сниппеты'
                    },
                    {
                        xtype: 'textAreaWithNote',
                        itemId: 'postQuery',
                        labelText: 'Постзапрос:',
                        noteText: 'Постзапрос может состоять из нескольких SQL-выражений, разделенных символом ; (точка с запятой)'
                    }
                ]
            }
        ];
        this.callParent(arguments);

    },

    getData : function(){
        var queries = new Object();
        queries.preQuery = this.down('#preQuery').down('textarea').getValue();
        queries.mainQuery = this.down('#mainQuery').down('textarea').getValue();
        queries.postQuery = this.down('#postQuery').down('textarea').getValue();
        return queries;
    },

    loadData: function(queries) {
        this.down('#preQuery').down('textarea').setValue(queries.preQuery);
        this.down('#mainQuery').down('textarea').setValue(queries.mainQuery);
        this.down('#postQuery').down('textarea').setValue(queries.postQuery);
    }

});