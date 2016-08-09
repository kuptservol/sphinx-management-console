Ext.define('sphinx-console.view.snippet.wizard.Step5Snippet' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.step5Snippet',
    title: 'Расписание дельта-индексации',
    requires: ['sphinx-console.view.Crone'],
    trackResetOnLoad: true,
    defaults: { // defaults are applied to items, not the container
        labelSeparator: '',
        msgTarget: 'under'
    },
    bodyPadding: 20,
    listeners: {
        validitychange: function (form, valid, eOpts) {
            this.up('snippetWizard').fireEvent('activeFormValidityChange');
        }
    },

    items: [
        {
            xtype: 'crone',
            width: '300'
        }
    ],

    getData : function(){
        return this.down('#cronExpression').getValue();
    },

    loadData: function(crone) {
        this.down('crone').loadData(crone);
    }

});