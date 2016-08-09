Ext.define('sphinx-console.view.PageSizePlugin', {
    extend: 'Ext.form.field.ComboBox',

    constructor: function() {
        sphinx-console.view.PageSizePlugin.superclass.constructor.call(this, {
        	store: new Ext.data.SimpleStore({
                 fields: ['text', 'value'],
                 pageSize: 10,
                 data: [['10', 10], ['20', 20],['30', 30]]
            }),
            queryMode: 'local',
            displayField: 'text',
            valueField: 'value',
            editable: false,
            allowBlank: false,
            triggerAction: 'all',
            width: 60,
            maxRecordsOnPage: 10
        });
    },

    initComponent: function () {
        this.callParent(arguments);
        
        
        this.setValue(this.getStore().pageSize);
    },

    init: function(paging) {
        paging.on('render', this.onInitView, this);
    },

    onInitView: function(paging) {
        paging.add('-',
            this,
            'Количество строк таблицы'
        );
        this.on('select', this.onPageSizeChanged, paging);
    },

   onPageSizeChanged: function(combo) {
        var pageSize = parseInt(combo.getValue());
        this.getStore().pageSize = pageSize;
        this.getStore().reload({params:{start:0, limit:pageSize, page: 1}});
        sphinx-console.app.fireEvent('changePageSize', pageSize);
   }
});