Ext.define('sphinx-console.view.LoadPageSizePlugin', {
    extend: 'Ext.form.field.ComboBox',
    callback: null,
    constructor: function(callback) {
        sphinx-console.view.PageSizePlugin.superclass.constructor.call(this, {
            store: Ext.data.Store({fields: ['value']}),
            queryMode: 'local',
            displayField: 'text',
            valueField: 'value',
            editable: false,
            allowBlank: false,
            triggerAction: 'all',
            width: 60,
            maxRecordsOnPage: 50
        });
        this.callback=callback
    },

    initComponent: function () {
        this.callParent(arguments);
        this.setValue(this.getStore().pageSize);
    },

    init: function(paging) {
        paging.on('render', this.onInitView, this);
        var records = [];
        for (var i = 1; i < this.maxRecordsOnPage; i++) {
            records.push({
                text: i.toString(),
                value: i
            })
        }
        this.store.setData(records);
    },

    onInitView: function(paging) {
        paging.add('-',this,'Количество строк таблицы');
        this.setValue(paging.getStore().pageSize);
        this.on('select', this.onPageSizeChanged, paging);
    },
    listeners:{
        afterrender:function(rec){
            this.getStore().setPageSize(rec.value);
        }
    },
    onPageSizeChanged: function(combo) {
        this.getStore().setPageSize(combo.getValue());
        this.pageSize = this.getStore().getPageSize();
        this.getStore().loadPage(1);
        if(combo.callback)combo.callback();
    }
});