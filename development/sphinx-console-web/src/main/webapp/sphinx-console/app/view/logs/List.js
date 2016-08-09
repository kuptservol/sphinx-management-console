Ext.define('sphinx-console.view.logs.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.logsList',
    store: 'TaskLog',
    forceFit: true,          //Fit to container
    columnLines: true,
    columnLines: true,
    minHeight: 100,
    maxHeight: 400,
    viewConfig : {
        scroll:false,
        style:{overflow: 'auto',overflowX: 'hidden'}
    },
    autoScroll: false,
    
    
    requires: ['sphinx-console.model.TaskLog','sphinx-console.view.logs.ErrorDetailWindow'],
    autoResizeColumns: true,
    
    initComponent: function () {
        this.columns =
            [
				
                {
                    header: 'UID',
                    dataIndex: 'taskUid',
                    width: 220,
                    flex: 4,
                    sortable: false,
                    menuDisabled: true
                },
                {
                    header: 'Сервер',
                    dataIndex: 'serverName',
                    width: 65,
                    flex: 1.5,
                    sortable: false,
                    menuDisabled: true
                },
                {
                    header: 'Дата и время начала',
                    dataIndex: 'startTime',
                    width: 125,
                    flex: 3,
                    sortable: false,
                    menuDisabled: true,
                    renderer: Ext.util.Format.dateRenderer('d.m.Y H:i:s')
                },
                {
                    header: 'Дата и время окончания',
                    dataIndex: 'endTime',
                    width: 135,
                    flex: 3,
                    sortable: false,
                    menuDisabled: true,
                    renderer: Ext.util.Format.dateRenderer('d.m.Y H:i:s')
                },
                {
                    header: 'Статус',
                    dataIndex: 'stageStatus',
                    flex: 2,
                    width: 75,
                    sortable: false,
                    menuDisabled: true,
                    renderer: function(val, dummy, r) {
	                    if (r.data['stageStatus'] == 'FAILURE') {
	                    	var id = Ext.id();
	                        Ext.defer(function() {
	                           Ext.widget('button', {
	                              renderTo: Ext.query("#"+id)[0],
	                              cls : 'bugbutton',
	                              scale: 'small',
	                              autoWidth : true, 
	                              autoHeight : true,
	                              handler: function() {
	                                 //sphinx-console.app.getsphinx-consoleControllerTasksController().onPauseTask(r.get('taskUid'), Ext.getStore('Tasks'));
	                                 Ext.Ajax.request({
	                                     url: sphinx-console.util.Utilities.SERVER_URL + '/view/taskErrorDescription/' + r.get('id'),
	                                     method: 'POST',
	                                     loadScripts: true,
	                                     success: function(response, opts) {
	                                       	var window = Ext.create('sphinx-console.view.logs.ErrorDetailWindow');
                                            window.html = response.responseText.split("\n").join("<br />");
                                            window.show();
                                         },
	                                     failure: function(error) {
	                                       	 Ext.Msg.alert('ERROR OCCURED WHILE RETRIEVING ERROR TEXT: ' + error);
	                                     }
	                                 });
	                              }
	                           });
	                        }, 30);
	                        return Ext.String.format('<span style="width:100%;" id="{0}">Ошибка&nbsp;&nbsp;</span>', id);
	                    } else {
	                    	return "Выполнено";
	                    }
                    }
                },
                {
                    header: 'Этап',
                    width: 260,
                    flex: 5,
                    dataIndex: 'stage',
                    sortable: false,
                    menuDisabled: true
                }
            ];
        this.dockedItems = [ {
        	xtype : 'pagingtoolbar',
			store : 'TaskLog',
			dock : 'bottom',
			plugins: [new sphinx-console.logs.PageSizePlugin()],
			displayMsg  : 'Записи {0} - {1} из {2}',
			refreshText: 'Обновить',
			prevText: 'Предыдущая',
			nextText: 'Следующая',
			beforePageText: 'Страница',
			afterPageText: 'из {0}',
			fieldLabel: '',
			displayInfo : true,
			listeners: {
	            beforechange: function() {
	               
	            }
	        }

		} ];

        this.callParent(arguments);
        this.dockedItems.items[1].down('#inputItem').width = 60;
       
    }

});

Ext.define('sphinx-console.logs.PageSizePlugin', {
    extend: 'Ext.form.field.ComboBox',

    constructor: function() {
        sphinx-console.view.PageSizePlugin.superclass.constructor.call(this, {
        	
  	           store: new Ext.data.SimpleStore({
  	                 fields: ['text', 'value'],
  	                 data: [['10', 10], ['20', 20],['30', 30]]
  	           }),
  	           mode: 'local',
  	           displayField: 'text',
  	           valueField: 'value',
  	           editable: false,
  	           allowBlank: false,
  	           triggerAction: 'all',
  	           width: 70
  	     
        });
    },

    initComponent: function () {
        this.callParent(arguments);
    },

    init: function(paging) {
    	 paging.on('render', this.onInitView, this);
    },

    onInitView: function(paging) {
	       paging.add('-',
	       this,
	       'Количество строк таблицы'
	 );
     if(paging.pageSize) this.setValue(paging.pageSize); else  this.setValue(this.getStore().getAt(0).getData().value);
	    this.on('select', this.onPageSizeChanged, paging);
	 },

   onPageSizeChanged: function(combo) {
	   this.pageSize = parseInt(combo.getValue());

	    

	   Ext.getStore('TaskLog').reload({params:{start:0, limit:this.pageSize}});
	   Ext.apply(Ext.getStore('TaskLog'), {pageSize: this.pageSize});
	   
	   combo.width = 100;	
    }
});

