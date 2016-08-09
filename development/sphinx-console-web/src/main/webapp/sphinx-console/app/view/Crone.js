Ext.define('sphinx-console.view.Crone' , {
    extend: 'Ext.form.Panel',
    alias: 'widget.crone',
    width: 300,
    height: 300,
    layout: 'vbox',
    margin: '20 0 0 0',
    defaults: {
        labelSeparator: '',
        labelWidth: 150,
        margin: '5 10 0 10',
        allowBlank: false,
        blankText: 'Поле обязательно для заполнения',
        msgTarget: 'under'
    },

    initComponent: function(){
        var th = this;
        this.items = [
            {
                xtype: 'label',
                text: 'Расписание в формате крон',
                margin: '10 10 10 10'
            },
            {
                itemId: 'minute',
                name: 'minute',
                xtype: 'textfield',
                fieldLabel: 'Минута',
                maxLength: 100,
                value: '*',
                enforceMaxLength: true,
                maskRe: /[\*\,\-0-9\/]/,
                listeners: { change: function(value) {return this.up('crone').createCronExpression()}}
            },
            {
                itemId: 'hour',
                name: 'hour',
                xtype: 'textfield',
                fieldLabel: 'Час',
                maxLength: 100,
                value: '*',
                enforceMaxLength: true,
                maskRe: /[\*\,\-0-9\/]/,
                listeners: { change: function(value) {return this.up('crone').createCronExpression()}}
            },
            {
                itemId: 'day',
                name: 'day',
                xtype: 'textfield',
                fieldLabel: 'День',
                maxLength: 100,
                value: '*',
                enforceMaxLength: true,
                maskRe: /[\*\,\-0-9\/LW\?]/,
                listeners: { change: function(value) {return this.up('crone').createCronExpression()}}
            },
            {
                itemId: 'month',
                name: 'month',
                xtype: 'textfield',
                fieldLabel: 'Месяц',
                maxLength: 100,
                value: '*',
                enforceMaxLength: true,
                maskRe: /[\*\,\-0-9\/JANFEBMRPYUNLGSOCTVD]/,
                listeners: { change: function(value) {return this.up('crone').createCronExpression()}}
            },
            {
                itemId: 'weekDay',
                name: 'weekDay',
                xtype: 'textfield',
                fieldLabel: 'День недели',
                maxLength: 100,
                value: '?',
                enforceMaxLength: true,
                maskRe: /[\*\.\-1-7\/\?L#SUNMOTEWDHFRIA]/,
                listeners: { change: function(value) {return this.up('crone').createCronExpression()}}
            },
            {
                itemId: 'cronExpression',
                name: 'cronExpression',
                xtype: 'textfield',
                fieldLabel: 'Cron expression',
                allowBlank: false,
                readOnly: true,
                validator: function(value) {return this.up('crone').cronExpressionValidator(value)}
            }
        ];

        this.callParent(arguments);
        this.createCronExpression();


    },

    createCronExpression: function(){
        var minutes = this.down('#minute').getSubmitValue();
        var hours = this.down('#hour').getSubmitValue();
        var day = this.down('#day').getSubmitValue();
        var month = this.down('#month').getSubmitValue();
        var weekday = this.down('#weekDay').getSubmitValue();
        var dummy = '1';
        var dday = day ? day : weekday == '?' ? dummy : '?';
        var cronExpression = Ext.util.Format.format('0 {0} {1} {2} {3} {4}',
            minutes ? minutes : dummy,
            hours ? hours : dummy,
            dday,
            month ? month : dummy,
            weekday ? weekday : dday == '?' ? 'MON' : '?');
        this.down('#cronExpression').setValue(cronExpression);
        return cronExpression;
    },

    cronExpressionValidator: function(value) {
        var result = sphinx-console.util.CronExpressionValidator.validate(value);;
        return result ? result : 'Неверное выражение для расписания';
    },

    loadData: function(cron) {
        //http://stackoverflow.com/questions/16690613/parse-crontab-line-with-javascript
        var minutes = this.down('#minute');
        var hours = this.down('#hour');
        var day = this.down('#day');
        var month = this.down('#month');
        var weekday = this.down('#weekDay');

        if (cron) {
            var cronArr = cron.split(' ');
            minutes.setValue(cronArr[1]);
            minutes.originalValue = cronArr[1];
            hours.setValue(cronArr[2]);
            hours.originalValue = cronArr[2];
            day.setValue(cronArr[3]);
            day.originalValue = cronArr[3];
            month.setValue(cronArr[4]);
            month.originalValue = cronArr[4];
            weekday.setValue(cronArr[5]);
            weekday.originalValue = cronArr[5];
        }
        this.down('#cronExpression').setValue(this.createCronExpression());
    }

});