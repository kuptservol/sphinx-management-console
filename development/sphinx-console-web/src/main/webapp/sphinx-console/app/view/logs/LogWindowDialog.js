Ext.define('sphinx-console.view.logs.LogWindowDialog', {
    configuration: null,
    constructor: function (configuration) {
        if(configuration) this.configuration = configuration;
        this.superclass.constructor.call(this);
    },
    show: function(configuration) {
        var taskUid;
        Ext.Msg.show({
            scope: this,
            title:'Подтверждение',
            msg: 'Проследить за ходом выполнения задания?',
            buttonText: {
                yes: 'Да',
                no: 'Нет'
            },
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function(buttonId, text, opt) {
                switch (buttonId) {
                    case 'yes':
                        configuration.asynchMode = false;
                        taskUid = this.executeAction(configuration);
                        Ext.create('sphinx-console.view.logs.LogWindow', {uid: taskUid}).show();
                        break;
                    case 'no':
                        configuration.asynchMode = true;
                        this.executeAction(configuration);
                        break;
                }
            }
        });
        return taskUid;
    },

    executeAction: function(configuration) {
        var taskUid = null;
        Ext.Ajax.request({
            async: configuration.asynchMode,
            url:  configuration.url,
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            params: configuration.params,
            waitTitle:'Connecting',
            waitMsg:'Creating...',
            method: 'POST',
            success: function(response, request) {
                var status = Ext.JSON.decode(response.responseText);
                taskUid = status ? status.taskUID : null;
                if (status && status.code != 0) {
                    sphinx-console.util.ErrorMessage.showFailureResponseWindow(status);
                } else {
                    if(configuration.onSuccessEventName) {
                        sphinx-console.app.fireEvent(configuration.onSuccessEventName);
                    }
                }
            },
            failure:function(response, request) {
                Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
            }
        });
        return taskUid;
    }

});