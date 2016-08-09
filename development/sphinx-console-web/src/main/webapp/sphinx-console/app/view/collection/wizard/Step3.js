Ext.define('sphinx-console.view.collection.wizard.Step3' , {
    extend: 'Ext.form.Panel',
    trackResetOnLoad: true,
    alias: 'widget.step3',
    title: 'Выбрать данные',
    id: 'step3',
    layout: 'fit',

    initComponent : function(){
        this.callParent();
        this.down('fieldMappingList').on('edit', function(editor, e) {
            var panel = this.up('step3');
            panel.down('#isIdHidden').setValue(e.record.data.indexFieldType == 'Нет' && e.record.data.isId ? '' : e.record.data.sourceField);
            panel.fireValidationEvent(panel);
        });
        //this.down('#tip').removeBodyCls('x-border-layout-ct'); //TODO ...
    },

    fireValidationEvent: function(panel) {
        var isValid = checkValidCustomSqlFields();
        this.down('#isValid').setValue(isValid ? true : '');
        this.up('wizard').fireEvent('activeFormValidityChange');
    },

    listeners: {
        activate: function (me, opts) {
            this.down('#innerPanel').removeBodyCls('x-border-layout-ct'); //TODO ...
            this.fireValidationEvent(this);
        },

        validitychange: function(form, valid, eOpts){


        },
        beforerender: function(form, valid, eOpts){

        }
    },

    getData : function(){
        var store = Ext.getStore('FieldMapping');
        var items = store.getRange();
        var dataArr = new Array();
        Ext.each(items, function(record){
            dataArr.push(record.data);
        });
        return dataArr;
    },

    loadData: function(data) {
        var store = Ext.getStore('FieldMapping');
        store.setData(data);
        var th = this;
        Ext.getStore(store.storeId).each(function(rec,idx){
            if(rec.data.isId){
                th.down('#isIdHidden').setValue(rec.data.sourceField);
                return;
            }
        });
        //to validation
        this.fireValidationEvent(this);

    },
    items: [
            {
                xtype: 'panel',
                itemId: 'innerPanel',
                flex: 1,
                layout: 'border',
                items: [
                    {
                        xtype: 'panel',
                        itemId: 'tip',
                        region: 'north',
                        margin: '10 10 10 10',
                        layout: 'vbox',
                        height: 100,
                        border: false,
                        style: {
                            background: 'white'
                        },
                        items: [
                            {
                                xtype: 'label',
                                editable: false,
                                text: 'Выберите данные для индексации.'
                            },{
                                xtype: 'label',
                                editable: false,
                                text: '1. Для каждого поля укажите его название и тип в индексе.'
                            },{
                                xtype: 'label',
                                editable: false,
                                text: '2. Если нужно, чтобы поле использовалось только для полнотекстового поиска, но не попадало в атрибуты, выбирайте тип "sql_field".'
                            },{
                                xtype: 'label',
                                editable: false,
                                text: '3. Укажите, какое из полей Sphinx должен использовать в качестве ID. Для этого поля должен быть выбран тип sql_attr_uint.'
                            },{
                                xtype: 'label',
                                editable: false,
                                text: '4. Хотя бы для одного поля должен быть выбран тип в индексе SQL_FIELD_STRING'
                            }
                        ]
                    },
                    {
                        xtype: 'fieldMappingList',
                        region: 'center',
                        columnWidth: .95
                    },
                    {
                        xtype:  'textfield',
                        hidden: true,
                        readOnly: true,
                        itemId: 'isIdHidden',  //to validation
                        name:   'isIdHidden',
                        allowBlank: false
                    },
                    {
                        xtype:  'textfield',
                        hidden: true,
                        readOnly: true,
                        itemId: 'isValid',  //to validation
                        name:   'isValid',
                        allowBlank: false
                    }
                ]
            }
           ]
});

Ext.define('sphinx-console.view.collection.wizard.FieldMapping.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.fieldMappingList',
    store: 'FieldMapping',
    forceFit: false,          //Fit to container
    columnLines: true,
    autoResizeColumns: true,
    layout: 'fit',
    //height: 400,
    autoScroll: true,
    default: {
        sortable: false,
        menuDisabled: true
    },
    viewConfig: {
        enableTextSelection: true
    },
    plugins: [
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })
    ],

    requires: 'sphinx-console.model.FieldMapping',
    autoResizeColumns: true,

    initComponent: function () {
        this.columns =
            [
				
                {
                    header: 'Поле в базе',
                    dataIndex: 'sourceField',
                    flex: 3,
                    width: 162
                },
                {
                    header: 'Тип в базе',
                    dataIndex: 'sourceFieldType',
                    flex: 3,
                    width: 162
                },
                {
                    header: 'Поле в индексе',
                    dataIndex: 'indexField',
                    width: 112,
                    flex: 3,
                    editor: {
                        xtype: 'textfield',
                        allowBlank: false,
                        selectOnFocus : true,
                        validator: function (value) {
                            return sphinx-console.model.FieldMapping.indexFieldIsValid(
                                value.toString()
                            ) ? true : 'Недопустимое значение';
                        }
                        //regex: /^[a-zA-Z0-9_]+$/
                        //maskRe: /[_a-zA-Z0-9]/
                    }
                },
                {
                    header: 'Тип в индексе',
                    dataIndex: 'indexFieldType',
                    width : 162,
                    flex: 3,
                    editor:  {
                        xtype: 'combobox',
                        mode: 'local',
                        queryMode: 'local',
                        emptyText: 'Нет',
                        allowBlank : false,
                        lazyInit: false,
                        triggerAction: 'all',
                        editable : false,
                        forceSelection: true,
                        lazyRender: true,
                        displayField: 'description',
                        valueField: 'indexFieldType',
                        listClass: 'x-combo-list-small',
                        store : new Ext.data.ArrayStore({
                            fields : ['indexFieldType', 'description'],
                            data : [
                                ['Нет', 'Нет'],
                                ['SQL_ATTR_UINT', 'sql_attr_uint'],
                                ['SQL_ATTR_BOOL', 'sql_attr_bool'],
                                ['SQL_ATTR_BIGINT', 'sql_attr_bigint'],
                                ['SQL_ATTR_TIMESTAMP', 'sql_attr_timestamp'],
                                ['SQL_ATTR_STR2ORDINAL', 'sql_attr_str2ordinal'],
                                ['SQL_ATTR_FLOAT', 'sql_attr_float'],
                                ['SQL_ATTR_MULTI', 'sql_attr_multi'],
                                ['SQL_ATTR_STRING', 'sql_attr_string'],
                                ['SQL_ATTR_JSON', 'sql_attr_json'],
                                ['SQL_ATTR_STR2WORDCOUNT', 'sql_attr_str2wordcount'],
                                ['SQL_FIELD_STRING', 'sql_field_string'],
                                ['SQL_FIELD_STR2WORDCOUNT', 'sql_field_str2wordcount'],
                                ['SQL_FILE_FIELD', 'sql_file_field'],
                                ['SQL_FIELD', 'sql_field']
                            ]
                        }),
                        listeners: {
                            select: function(combo, records, eOpts) {

                            }
                        }
                    }
                },
                {
                    header: 'ID',
                    dataIndex: 'isId',
                    sortable: false,
                    menuDisabled: true,
                    flex: 1,
                    width : 30,
                    renderer: function (value, metaData, record, rowIdx, colIdx, store) {
                        return '<input onchange="changeState(' + rowIdx + ',\'' + store.storeId + '\');" type="radio" name="isId" id="radio" ' +  (value ? "checked='checked'" : "") + '/>';
                    }
                },
                {
                    header: 'Комментарий',
                    dataIndex: 'indexFieldCommentary',
                    sortable: false,
                    menuDisabled: true,
                    width : 162,
                    flex: 3,
                    editor: {
                        xtype: 'textfield',
                        allowBlank: true/*,
                         regex: /^[a-zA-Z ]+$/*/
                    }
                }
            ];

        this.callParent(arguments);
    }
});

function changeState(rowIdx, storeId){
    var store =  Ext.getStore(storeId);
    store.each(function(rec,idx){
        rec.set('isId', false);
    });
    var step = Ext.ComponentQuery.query('step3')[0];
    store.getAt(rowIdx).set('isId', true);

    var row = store.getAt(rowIdx);
    step.down('#isIdHidden').setValue(row.get('indexFieldType') == 'Нет' && row.get('isId') ? '' : row.get('sourceField'));
    step.fireValidationEvent(step);
    
    store.commitChanges();
}

function checkValidCustomSqlFields () {
    var store = Ext.getStore('FieldMapping');
    var items = store.getRange();
    var hasType = false;
    var hasId = false;
    Ext.each(items, function(record){
        if (record.data.indexFieldType != 'Нет' && record.data.indexFieldType == 'SQL_FIELD_STRING') {
            hasType = true;
        }
        if (record.data.isId && record.data.indexFieldType == 'SQL_ATTR_UINT') {
            hasId = true;
        }
     });
    return hasType && hasId;
}

//function checkValidCustomSqlFields () {
//    if (Ext.getCmp('customSql') && Ext.getCmp('customSql').getValue()) {
//        var toolbar;
//        if (Ext.getCmp('step3').up('wizard')) {
//            toolbar = Ext.getCmp('step3').up('wizard').getDockedItems('toolbar[dock="bottom"]')[0];
//        }
//        var store = Ext.getStore('FieldMapping');
//        var items = store.getRange();
//        //Везде ли выбран тип
//        var hasType = false;
//        Ext.each(items, function(record){
//            if (record.data.indexFieldType == 'Нет') {
//                if (toolbar) {
//                    toolbar.items.get('next').setDisabled(true);
//                    hasType = false;
//                    return false;
//                }
//            } else {
//                if (toolbar && record.data.indexFieldType == 'SQL_ATTR_STRING') {
//                    //toolbar.items.get('next').setDisabled(false);
//                    hasType = true;
//                }
//            }
//        });
//        if (!hasType) {
//            return false;
//        }
//        // Выброно ли поле ID
//        var hasId = false;
//        Ext.each(items, function(record){
//            if (record.data.isId && record.data.indexFieldType == 'SQL_ATTR_UINT') {
//                if (toolbar) {
//                    //toolbar.items.get('next').setDisabled(false);
//                    hasId = true;
//                    return false;
//                }
//            }
//        });
//        if (toolbar && !hasId) {
//            toolbar.items.get('next').setDisabled(true);
//        }
//    }
//}











