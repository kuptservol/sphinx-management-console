Ext.define('sphinx-console.view.tasks.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.tasksList',
    id: 'tasksList',
    store: 'Tasks',
    forceFit: true,          //Fit to container
    columnLines: true,
    minHeight: 100,
    maxHeight: 550,
    viewConfig : {
        scroll:false,
        style:{overflow: 'auto',overflowX: 'hidden'}
    },
    autoScroll: false,
    
    requires: ['sphinx-console.model.Task','sphinx-console.view.logs.Panel','sphinx-console.view.logs.LogWindow'],
    autoResizeColumns: true,
    
    initComponent: function () {
        this.columns =
            [
				{
				    header: 'Действия',
				    dataIndex: 'taskUid',
				    sortable: false,
				    menuDisabled: true,
				    xtype : 'actioncolumn',
				    width: 50,
				    items : [{
				        icon: 'app/resources/images/log.png',
				        tooltip : 'Просмотреть лог по заданию',
				        id:'taskUid',
				        handler : function(grid, rowIndex, colIndex, node, e, record, rowNode) {
				        	var win = Ext.create('sphinx-console.view.logs.LogWindow', {uid: record.get('taskUid')});
				        	win.on('show',function(){
				        	    Ext.getStore('TaskLog').pageSize = 10;
				        	});
				        	Ext.EventManager.onWindowResize(win.center, win);
				        	win.show();
				        }
				    }]
				},
                {
                    header: 'UID',
                    dataIndex: 'taskUid',
                    width: 160,
                    sortable: false,
                    menuDisabled: true
                },
                {
                    header: 'Задание',
                    dataIndex: 'taskName',
                    sortable: false,
                    menuDisabled: true
                },
                {
                    header: 'Коллекция',
                    dataIndex: 'collectionName',
                    sortable: false,
                    menuDisabled: true
                },
                {
                    header: 'Дата и время начала',
                    dataIndex: 'startTime',
                    sortable: false,
                    menuDisabled: true,
                    renderer: Ext.util.Format.dateRenderer('d.m.Y H:i:s')
                },
                {
                    header: 'Дата и время окончания',
                    dataIndex: 'endTime',
                    sortable: false,
                    menuDisabled: true,
                    renderer: Ext.util.Format.dateRenderer('d.m.Y H:i:s')
                },
                {
                	header: 'Статус',
                    dataIndex: 'status',
                    sortable: false,
                    menuDisabled: true,

                    renderer: function(val, dummy, r) {

	                    if (r.data['status'] == 'FAILURE') {
	                    	var idImage = Ext.id();
	                    	Ext.defer(function() {
	                    		Ext.widget('image', {
	                                renderTo: Ext.query("#" + idImage)[0],
                                    cls : 'stopbutton transparentImage'
	                                //src:  'app/resources/images/stop.png'
	                            });
		                    }, 30);

	                    	return Ext.String.format('<span class="imgHolder" id="{0}">&nbsp;&nbsp;</span>&nbsp;&nbsp;<span class="imgHolder" id="{1}">Ошибка&nbsp;&nbsp;</span>', idImage, id);
	                    } else if (r.data['status'] == 'RUNNING') {

	                    	var id = Ext.id();
	                    	var idImage = Ext.id();
	                    	Ext.defer(function() {
	                    		Ext.widget('image', {
	                                renderTo: Ext.query("#" + idImage)[0],
	                                src:  'app/resources/images/flag_running.png'
	                            });
		                    }, 30);

	                        Ext.defer(function() {
	                           Ext.widget('button', {
	                              renderTo: Ext.query("#"+id)[0],
	                              cls : 'pause24button transparentImage',
                                  border: 'none',
                                   //src:  'app/resources/images/pause24.png',
	                              scale: 'small',
	                              autoWidth : true,
	                              autoHeight : true,
	                              handler: function() {
	                                 sphinx-console.app.getsphinx-consoleControllerTasksController().onPauseTask(r.get('taskUid'), Ext.getStore('Tasks'));
	                              }
	                           });
	                        }, 30);


	                        Ext.defer(function() {
	                           Ext.widget('button', {
	                              renderTo: Ext.query("#"+id)[0],
	                              cls : 'stopbutton transparentImage',
                                  border: 'none',
                                   //src:  'app/resources/images/stop.png',
	                              scale: 'small',
	                              autoWidth : true,
	                              autoHeight : true,
	                              handler: function() {
	                            	  sphinx-console.app.getsphinx-consoleControllerTasksController().onStopTask(r.get('taskUid'), Ext.getStore('Tasks'));
	                              }
	                           });
	                        }, 30);

	                        return Ext.String.format('<span class="imgHolder" id="{0}">&nbsp;&nbsp;</span>&nbsp;&nbsp;<span class="imgHolder" id="{1}">Выполняется&nbsp;&nbsp;</span>', idImage, id);
	                    } else if (r.data['status'] == 'STOPPED') {
	                    	return '<span style="vertical-align: middle; padding-right: 20px;width:100%;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Остановлено</span>';
	                    } else if (r.data['status'] == 'PAUSED') {

	                    	var id = Ext.id();
	                    	var idImage = Ext.id();

	                    	Ext.defer(function() {
	                    		Ext.widget('image', {
	                                renderTo: Ext.query("#" + idImage)[0],
	                                src:  'app/resources/images/flag_suspend.png'
	                            });
		                    }, 30);

	                        Ext.defer(function() {
	                           Ext.widget('button', {
	                              renderTo: Ext.query("#"+id)[0],
	                              cls : 'startbutton transparentImage',
                                  border: 'none',
                                   //src:  'app/resources/images/start.png',
	                              scale: 'small',
	                              autoWidth : true,
	                              autoHeight : true,
	                              handler: function() {
	                            	  sphinx-console.app.getsphinx-consoleControllerTasksController().onStartTask(r.get('taskUid'), Ext.getStore('Tasks'));
	                              }
	                           });
	                        }, 30);


	                        Ext.defer(function() {
	                           Ext.widget('button', {
	                              renderTo: Ext.query("#"+id)[0],
	                              cls : 'stopbutton transparentImage',
                                  border: 'none',
                                  //src:  'app/resources/images/stop.png',
	                              scale: 'small',
	                              autoWidth : true,
	                              autoHeight : true,
	                              handler: function() {
	                            	  sphinx-console.app.getsphinx-consoleControllerTasksController().onStopTask(r.get('taskUid'), Ext.getStore('Tasks'));
	                              }
	                           });
	                        }, 30);

	                        return Ext.String.format('<span class="imgHolder" id="{0}">&nbsp;&nbsp;</span>&nbsp;&nbsp;<span class="imgHolder" id="{1}">На паузе&nbsp;&nbsp;</span>', idImage, id);
	                    } else if (r.data['status'] == 'SUCCESS') {
	                    	return '<span style="vertical-align: middle; padding-right: 20px;width:100%;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Завершено</span>';
	                    }
                    }
                },
                {
                    header: 'Этап',
                    dataIndex: 'stage',
                    sortable: false,
                    menuDisabled: true
                }
            ];
        this.dockedItems = [{
            xtype: 'toolbar',
            items: [{
                xtype: 'button',
                text: 'Обновить',
                handler: function() {
                	Ext.getStore('Tasks').proxy.setExtraParam('collectionName', Ext.getCmp('collectionSearchField').getValue());
 	                Ext.getStore('Tasks').proxy.setExtraParam('taskStatus' , Ext.getCmp('taskStatusField').getValue()); 
 	                Ext.getStore('Tasks').proxy.setExtraParam('dateFrom' , Ext.getCmp('fromDate').getValue());
 	        	    Ext.getStore('Tasks').proxy.setExtraParam('dateTo' , Ext.getCmp('toDate').getValue());
 	          	
                	sphinx-console.app.getsphinx-consoleControllerTasksController().onRefreshTasks(Ext.getStore('Tasks'));
                }
            }]
        }, {
        	id: 'tasksPagingToolbar',
			xtype : 'pagingtoolbar',
			store : 'Tasks',
			dock : 'bottom',
			plugins: [new Ext.ux.PageSizePlugin()],
			displayMsg  : 'Записи {0} - {1} из {2}',
			refreshText: 'Обновить',
			beforePageText: 'Страница',
			afterPageText: 'из {0}',
			displayInfo : true,
			listeners: {
	            beforechange: function() {
	               Ext.getStore('Tasks').proxy.setExtraParam('collectionName', Ext.getCmp('collectionSearchField').getValue());
	               Ext.getStore('Tasks').proxy.setExtraParam('taskStatus' , Ext.getCmp('taskStatusField').getValue()); 
	               Ext.getStore('Tasks').proxy.setExtraParam('dateFrom' , Ext.getCmp('fromDate').getValue());
	        	   Ext.getStore('Tasks').proxy.setExtraParam('dateTo' , Ext.getCmp('toDate').getValue());
	          	
	            }
	        }

		} ];

        this.callParent(arguments);
    }
});

Ext.ux.PageSizePlugin = function() {
 	 Ext.ux.PageSizePlugin.superclass.constructor.call(this, {
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
 	           width: 60
 	     });
 	 };


 	

 	Ext.extend(Ext.ux.PageSizePlugin, Ext.form.ComboBox, {
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

 	    Ext.getStore('Tasks').proxy.setExtraParam('collectionName', Ext.getCmp('collectionSearchField').getValue());
 	    Ext.getStore('Tasks').proxy.setExtraParam('taskStatus', Ext.getCmp('taskStatusField').getValue());
 	    Ext.getStore('Tasks').proxy.setExtraParam('dateFrom', Ext.getCmp('fromDate').getValue());
 	    Ext.getStore('Tasks').proxy.setExtraParam('dateTo', Ext.getCmp('toDate').getValue());
   	

 		Ext.getStore('Tasks').reload({params:{start:0, limit:this.pageSize}});
 		Ext.apply(Ext.getStore('Tasks'), {pageSize: this.pageSize});

 	 }
 	});



