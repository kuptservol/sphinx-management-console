Ext.define('sphinx-console.view.snippet.wizard.SnippetWizardPanel', {
    extend: 'sphinx-console.view.snippet.wizard.SnippetWizard',
    alias: 'widget.snippetWizardPanel',
    requires: ['sphinx-console.view.snippet.wizard.Step1Snippet', 'sphinx-console.view.snippet.wizard.Step2Snippet', 'sphinx-console.view.snippet.wizard.Step3Snippet',
               'sphinx-console.view.snippet.wizard.Step4Snippet', 'sphinx-console.view.snippet.wizard.Step5Snippet',
                'sphinx-console.model.SnippetConfigurationField', 'sphinx-console.model.SnippetConfiguration', 'sphinx-console.model.SnippetConfigurationWrapper'],
    record: null,
    isEdit : false,

    loadSnippetData: function() {

        var snippetWrapper;
        var step1 = this.down('panel[alias=widget.step1Snippet]');
        var step2 = this.down('panel[alias=widget.step2Snippet]');
        var step3 = this.down('panel[alias=widget.step3Snippet]');
        var step4 = this.down('panel[alias=widget.step4Snippet]');
        var step5 = this.down('panel[alias=widget.step5Snippet]');

        Ext.Ajax.request({
            url:  sphinx-console.util.Utilities.SERVER_URL + "/view/snippetConfigurationWrapper/" + this.record.data.collectionName,
            method: 'GET',
            success: function(response) {

                snippetWrapper = Ext.JSON.decode(response.responseText);
                step1.setDisabled(true); //to suppress validation
                step2.setDisabled(true);
                step3.setDisabled(true);
                step4.setDisabled(true);
                step5.setDisabled(true);

                // step 1
                step1.loadData(snippetWrapper.collectionName);

                // step 2
                var queries = new Object();
                queries.preQuery = snippetWrapper.snippetConfiguration.preQuery;
                queries.mainQuery = snippetWrapper.snippetConfiguration.mainQuery;
                queries.postQuery = snippetWrapper.snippetConfiguration.postQuery;
                step2.loadData(queries);

                // step 4
                var queries = new Object();
                queries.fullRebuildPreQuery = snippetWrapper.snippetConfiguration.fullPreQuery;
                queries.fullRebuildMainQuery = snippetWrapper.snippetConfiguration.fullMainQuery;
                queries.fullRebuildPostQuery = snippetWrapper.snippetConfiguration.fullPostQuery;
                step4.loadData(queries);

                // step 5
                step5.loadData(snippetWrapper.cron.cronSchedule);

                step1.setDisabled(false);
                step2.setDisabled(false);
                step3.setDisabled(false);
                step4.setDisabled(false);
                step5.setDisabled(false);
            }
        });
    },

    initComponent: function() {
        this.items = [
            {xtype: 'step1Snippet'},
            {xtype: 'step2Snippet'},
            {xtype: 'step3Snippet'},
            {xtype: 'step4Snippet'},
            {xtype: 'step5Snippet', finishable: true}
        ];

        this.callParent(arguments);
        this.isEdit = (this.record != null);
        if(this.isEdit) {
            this.loadSnippetData();
        }
    },

    listeners: {
        snippetwizardcancelled: function () {
            Ext.each(this.items.items, function(step) {
                step.fireEvent('deactivate', step);
            });
            this.up('window').close();
            Ext.getCmp('snippetList').snippetWizard = null;
        },

        snippetWizardfinished: function () {

            var th = this;

            Ext.Msg.show({
                scope: this,
                title: 'Подтверждение',
                msg: 'Проследить за ходом выполнения задания?',
                buttonText: {
                    yes: 'Да',
                    no: 'Нет'
                },
                buttons: Ext.Msg.YESNO,
                icon: Ext.Msg.QUESTION,
                fn: function (buttonId, text, opt) {
                    switch (buttonId) {
                        case 'yes':
                            var status = th.onSnippetWizardFinished(null, false);
                            var taskUid = status != null ? status.taskUID : null;
                            Ext.create('sphinx-console.view.logs.LogWindow', {uid: taskUid}).show();
                            break;
                        case 'no':
                            th.onSnippetWizardFinished();
                            break;
                    }
                }
            });
        }
    },
    
    onSnippetWizardFinished: function (server, async_mode) {

        if (async_mode != false) {
            async_mode = true;
        }
        var step1 = this.down('panel[alias=widget.step1Snippet]');
        var step2 = this.down('panel[alias=widget.step2Snippet]');
        var step3 = this.down('panel[alias=widget.step3Snippet]');
        var step4 = this.down('panel[alias=widget.step4Snippet]');
        var step5 = this.down('panel[alias=widget.step5Snippet]');

        var snippetWrapper = Ext.create('sphinx-console.model.SnippetConfigurationWrapper');
        snippetWrapper.set('id',null);
        var snippetConfiguration = Ext.create('sphinx-console.model.SnippetConfiguration');
        snippetConfiguration.set('id',null);

        // step 1
        var collectionName = step1.getData();

        // step 2
        var queries = step2.getData();
        snippetConfiguration.set('preQuery', queries.preQuery);
        snippetConfiguration.set('mainQuery', queries.mainQuery);
        snippetConfiguration.set('postQuery', queries.postQuery);

        // step 3
        var fields = step3.getData();
        var fieldsArray = [];
        for(var i = 0; i < fields.selectFieldNames.length; i++){
            var field = Ext.create('sphinx-console.model.SnippetConfigurationField');
            field.set('id',null);
            field.set('fieldName', fields.selectFieldNames[i]);
            fieldsArray.push(field);
        }
        snippetConfiguration.snippetFields().add(fieldsArray);

        // step 4
        var fullRebuildQueries = step4.getData();
        snippetConfiguration.set('fullPreQuery', fullRebuildQueries.fullRebuildPreQuery);
        snippetConfiguration.set('fullMainQuery', fullRebuildQueries.fullRebuildMainQuery);
        snippetConfiguration.set('fullPostQuery', fullRebuildQueries.fullRebuildPostQuery);

        snippetWrapper.setSnippetConfiguration(snippetConfiguration);

        // step 5
        var cronWrapper = Ext.create('sphinx-console.model.CronScheduleWrapper', {});
        cronWrapper.set('cronSchedule', step5.getData());
        snippetWrapper.setCron(cronWrapper);

        var data = snippetWrapper.getAssociatedData();
        data.collectionName = collectionName;
        data.snippetConfiguration.fields = data.snippetConfiguration.snippetFields;
        delete data.snippetConfiguration.snippetFields;

        if (sphinx-console.util.Utilities.DEBUG)
            console.log(Ext.JSON.encodeValue(data, '\n'));

        Ext.each(this.items.items, function(step) {
            step.fireEvent('deactivate', step);
        });
        this.up('window').close();
        Ext.getCmp('snippetList').snippetWizard = null;

        var url = sphinx-console.util.Utilities.SERVER_URL + (!this.isEdit ? '/configuration/createSnippetConfiguration' : '/configuration/editSnippetConfiguration');
        var status = null;
        Ext.Ajax.request({
            url:  url,
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            params: Ext.JSON.encodeValue(data, '\n'),
            waitTitle:'Connecting',
            waitMsg:'Creating...',
            method: 'POST',
            async: async_mode,
            success: function(response) {
                status = Ext.JSON.decode(response.responseText);

                if (status && status.code != 0) {
                    Ext.MessageBox.alert('Ошибка',
                            'Ошибка, интерфейс: ' + status.systemInterface +
                            ", message: " + status.message +
                            ", description: " + status.description +
                            ", StackTrace: " + status.stackTrace);
                }
            }
        });

        return status;
    }
});




