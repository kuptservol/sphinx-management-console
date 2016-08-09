Ext.define('sphinx-console.view.tasks.Search', {
    extend: 'Ext.form.Panel',
    alias: 'widget.tasksSearch',
    items:[
        {
            xtype : 'fieldset',
            title : 'Параметры поиска',
            height: '100%',
            defaults: {
                labelWidth: 205,
                width: '50%',
                margin: 5
            },
            items: [
                {
                    fieldLabel: 'Коллекция',
                    xtype : 'textfield',
                    id : 'collectionSearchField',
                    emptyText : 'Введите полностью или часть наименования коллекции'
                },
                {
    			    xtype : 'combo',
    			    id : 'taskStatusField', 
    			    mode: 'local',
    			    fieldLabel : 'Статус',
    			    hiddenName : 'taskStatus',
    			    allowBlank : true,
    			    valueField : 'id',
    			    displayField : 'name',
    			    emptyText : 'Все',
    			    triggerAction: 'all',
    			    editable : false,
    			    store : new Ext.data.ArrayStore({
    			        fields : ['id', 'name'],
    			        data : [
    			            ['&nbsp;', 'Все'], 
    			            ['RUNNING', 'Выполняется'],
    			            ['FAILURE', 'Ошибка'],
    			            ['STOPPED', 'Остановлено'],
    			            ['PAUSED', 'На паузе'],
    			            ['SUCCESS', 'Завершено']
    			           
    			        ]
    			    }),
    			    listeners: {
                        // public event change - when selection1 dropdown is changed
                        change:    function(field, newValue, oldValue){
                        	Ext.getStore('Tasks').proxy.setExtraParam('collectionName', Ext.getCmp('collectionSearchField').getValue());
    		         	    if (Ext.getCmp('taskStatusField').getValue() == "&nbsp;") {
    		         	    	Ext.getCmp('taskStatusField').setValue(null);   
    			            }
    		         	    Ext.getStore('Tasks').proxy.setExtraParam('taskStatus', Ext.getCmp('taskStatusField').getValue());
    		         	    Ext.getStore('Tasks').proxy.setExtraParam('dateFrom', Ext.getCmp('fromDate').getValue());
    		         	    Ext.getStore('Tasks').proxy.setExtraParam('dateTo', Ext.getCmp('toDate').getValue());
    		            	sphinx-console.app.getsphinx-consoleControllerTasksController().onLoadByParam();
                        }
                    }
                },
                {
                    xtype : 'combo',
                    id : 'taskNames',
                    fieldLabel : 'Задание',
                    allowBlank : true,
                    valueField : 'taskName',
                    displayField : 'taskNameTitle',
                    emptyText : 'Все',
                    editable : false,
                    multiSelect: true,
                    store: Ext.create('sphinx-console.store.TaskNames')
                },
                {
                    xtype : 'container',
                    layout : 'hbox',
                    items :[
                        {
                            xtype : 'datefield',
                            id : 'fromDate',
                            name : 'fromDate',
                            fieldLabel : 'Дата и время выполнения: c',
                            labelSeparator: '',
                            labelWidth: 205,
                            format : 'd.m.Y'
                        },
                        {
                            xtype : 'datefield',
                            id : 'toDate',
                            name : 'toDate',
                            margin: '0 5 0 5',
                            fieldLabel : 'по',
                            labelWidth: 20,
                            labelSeparator: '',
                            format : 'd.m.Y'
                        }
                ]
                },
                {
                    xtype : 'container',
                    items :[
        		        {
        		            itemId: 'searchTasks',
        		            xtype: 'button',
        		            text : 'Найти',
                            cls : 'searchbutton',
        		            handler: function(){
        		            	Ext.getStore('Tasks').proxy.setExtraParam('collectionName', Ext.getCmp('collectionSearchField').getValue());
                                Ext.getStore('Tasks').proxy.setExtraParam('taskNames', Ext.getCmp('taskNames').getValue());
        		         	    Ext.getStore('Tasks').proxy.setExtraParam('taskStatus', Ext.getCmp('taskStatusField').getValue());
        		         	    Ext.getStore('Tasks').proxy.setExtraParam('dateFrom', Ext.getCmp('fromDate').getValue());
        		         	    Ext.getStore('Tasks').proxy.setExtraParam('dateTo', Ext.getCmp('toDate').getValue());
        		            	sphinx-console.app.getsphinx-consoleControllerTasksController().onLoadByParam();
                            }
        		        },
        		        {
        		            itemId: 'resetSearchTasks',
        		            xtype: 'button',
                            cls : 'clearfilterbutton',
        		            text : 'Очистить фильтр',
        		            handler: function(searchButton) {
                                searchButton.up('form').getForm().reset();
                                Ext.getStore('Tasks').getProxy().setExtraParams({});
                                sphinx-console.app.getsphinx-consoleControllerTasksController().onLoad();
                            }
        		        }
                
                    ]
                }
            ]
        }
    ]
});