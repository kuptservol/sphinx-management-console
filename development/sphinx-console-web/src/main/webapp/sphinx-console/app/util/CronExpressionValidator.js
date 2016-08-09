Ext.define('sphinx-console.util.CronExpressionValidator', {
        singleton: true,
        validate: function(cronExpression) {
            var result = true;
            Ext.Ajax.request({
                async: false,
                url: sphinx-console.util.Utilities.SERVER_URL + '/configuration/validateCronExpression/',
                useDefaultXhrHeader: false,
                headers: { 'Content-Type': 'application/json' },
                jsonData: {"parameter": cronExpression},
                method: 'POST',
                success: function (response) {
                    result = Ext.JSON.decode(response.responseText).result;
                }
            });
            return result;
        }
    }
);