Ext.define('sphinx-console.view.snippet.wizard.Step1Snippet' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.step1Snippet',
    title: 'Выберите коллекцию',
    trackResetOnLoad: true,
    defaults: { // defaults are applied to items, not the container
        labelSeparator: '',
        width: '100%',
        msgTarget: 'under'
    },
    bodyPadding: 20,
    listeners: {
        validitychange: function(form, valid, eOpts){
            this.up('snippetWizard').fireEvent('activeFormValidityChange');
        },
        activate: function(){
            this.updateCollectionNameStore();
        }
    },
    collectionNameStore: Ext.create('Ext.data.Store', {model: 'sphinx-console.model.CollectionName'}),

    initComponent: function () {
        var th = this;
        th.items = [
            {
                xtype: 'combo',
                itemId: 'collectionNameCombo',
                fieldLabel: 'Выберите коллекцию',
                displayField: 'collectionName',
                editable: false,
                allowBlank: false,
                store: th.collectionNameStore,
                fieldStyle: {
                    display: 'inherit'
                }
            }
        ];
        this.callParent(arguments);
    },

    updateCollectionNameStore: function(){
        var th = this;
        Ext.Ajax.request({
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/collectionsForSnippetCreation',
            async: false,
            method: 'GET',
            success: function(response) {
                var collections = Ext.JSON.decode(response.responseText);
                var collectionModels = [];
                for(var i = 0; i < collections.length; i++){
                    collectionModels.push(Ext.create('sphinx-console.model.CollectionName', {collectionName: collections[i]}));
                }
                th.collectionNameStore.loadData(collectionModels);
            }
        });
    },

    getData : function(){
        return this.down('#collectionNameCombo').getValue();
    },

    loadData: function(collectionName) {
        this.down('#collectionNameCombo').setValue(collectionName);
    }

});