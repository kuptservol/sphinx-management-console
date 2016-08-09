Ext.define('sphinx-console.util.ErrorMessage', {
    singleton: true,

    showFailureResponseWindow: function(status) { 
        Ext.MessageBox.alert('Ошибка',
            'Ошибка:' + status.systemInterface +
            ", message: " + status.message +
            ", description: " + status.description +
            ", StackTrace: " + status.stackTrace);
    },

    showFailureResponse: function(response){
        Ext.Msg.show({
            title:'Ошибка',
            modal: 'true',
            minWidth: '70%',
            maxWidth: '70%',
            message: response.responseText,
            buttons: Ext.Msg.OK
        });
    }
});