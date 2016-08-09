Ext.define('sphinx-console.view.collection.wizard.WizardPanel', {
    extend: 'sphinx-console.view.collection.wizard.Wizard',
    store: ['Collection','ConfigurationTemplateIndexer','ConfigurationFields','ConfigurationTemplateSearch','ConfigurationTemplateConfiguration'],
    alias: 'widget.collectionWizard',
    requires: ['sphinx-console.view.collection.wizard.Step1', 'sphinx-console.view.collection.wizard.Step2', 'sphinx-console.view.collection.wizard.Step3',
               'sphinx-console.view.collection.wizard.Step4', 'sphinx-console.view.collection.wizard.Step5', 'sphinx-console.view.collection.wizard.Step6',
               'sphinx-console.view.collection.wizard.Step7', 'sphinx-console.view.collection.wizard.SelectServerWindow',
               'sphinx-console.view.collection.wizard.StepDistributed2','sphinx-console.view.collection.wizard.StepDistributed3',
               'sphinx-console.view.collection.wizard.StepDistributed4','sphinx-console.view.collection.wizard.StepDistributed5'
            ],

    record: null,
    isEdit : false,

    loadSimpleCollectionData: function() {

        sphinx-console.app.on('wizardclosed', function() {
            if (Ext.getCmp('sourceConfigurationFields')) {
                Ext.getCmp('sourceConfigurationFields').getStore().data.clear();
            }
            // TODO: Сделать здесь полноценный cleanup всех данных, которые использует визард
            Ext.getStore('FieldMapping').data.clear();
        }, this);

        var data;
        var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.step2]');
        var step3 = this.down('panel[alias=widget.step3]');
        var step4 = this.down('panel[alias=widget.step4]');
        var step5 = this.down('panel[alias=widget.step5]');
        var step6 = this.down('panel[alias=widget.step6]');
        var step7 = this.down('panel[alias=widget.step7]');
        Ext.Ajax.request({
            url:  sphinx-console.util.Utilities.SERVER_URL + "/view/collectionWrapper/" + this.record.data.name,
            method: 'POST',
            success: function(response) {

                data = Ext.JSON.decode(response.responseText);
                step1.setDisabled(true); //to suppress validation
                step2.setDisabled(true);
                step3.setDisabled(true);
                step4.setDisabled(true);
                step5.setDisabled(true);
                step6.setDisabled(true);
                step7.setDisabled(true);
                step1.loadData(data.result.collection);
                if (data.result.searchConfiguration && data.result.indexConfiguration) {
                    step2.loadData(data.result.searchConfiguration.datasource, data.result.searchConfiguration.sourceConfigurationFields, data.result.collection.delta.deleteScheme, data.result.collection.type);

                    step3.loadData(data.result.searchConfiguration.fieldMappings);
                    step4.loadData(data.result.indexConfiguration.indexerConfigurationTemplate,data.result.indexConfiguration.id,data.result.indexConfiguration.name,data.result.indexConfiguration.filePath);
                    step5.loadData(data.result.searchConfiguration.searchConfigurationTemplate,data.result.searchConfiguration.id,data.result.searchConfiguration.name,
                        data.result.searchConfiguration.filePath,data.result.searchConfigurationPort?data.result.searchConfigurationPort.searchConfigurationPort:null,
                        data.result.distributedConfigurationPort?data.result.distributedConfigurationPort.distributedConfigurationPort:null);
                    step6.loadData(data.result.searchConfiguration.configurationTemplate);
                }

                var cron, cronDelta;
                if (data.result.cronSchedule) {
                    cron = data.result.cronSchedule.cronSchedule;
                }
                if (data.result.mainCronSchedule) {
                    cronDelta = data.result.mainCronSchedule.cronSchedule;
                }
                if (data.result.collection.delta) {
                    step7.loadData(cron, cronDelta, data.result.indexServer, data.result.searchServer, data.result.collection.delta.externalAction);
                }

                step1.setDisabled(false);
                step2.setDisabled(false);
                step3.setDisabled(false);
                step4.setDisabled(false);
                step5.setDisabled(false);
                step6.setDisabled(false);
                step7.setDisabled(false);
            }
        });
    },

    loadDistributedCollectionData: function() {

        var th = this;
        var data;
        var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.stepDistributed2]');
        var step3 = this.down('panel[alias=widget.stepDistributed3]');
        var step4 = this.down('panel[alias=widget.stepDistributed4]');
        var step5 = this.down('panel[alias=widget.stepDistributed5]');
        Ext.Ajax.request({
            url:  sphinx-console.util.Utilities.SERVER_URL + "/view/distributedCollectionWrapper/" + this.record.data.name,
            method: 'GET',
            success: function(response) {

                th.isDistributedCollection = true;

                data = Ext.JSON.decode(response.responseText);
                
                if (data && data.stackTrace) {
                	Ext.MessageBox.alert('Ошибка', 'Произошла ошибка: ' + data.stackTrace);
                	Ext.each(th.items.items, function(step) {
                        step.fireEvent('deactivate', step);
                    });
                    th.up('window').close();
                	return;
                }
                
                step1.setDisabled(true); //to suppress validation
                step2.setDisabled(true);
                step3.setDisabled(true);
                step4.setDisabled(true);
                step5.setDisabled(true);

                step1.loadData(data.collection);

                step2.collectionName = data.collection.name;
                step2.loadData(data.nodes);
                if (data.searchConfiguration) {
                    step3.loadData(
                        data.searchConfiguration.searchConfigurationTemplate,
                        data.searchConfiguration.id,
                        data.searchConfiguration.name,
                        data.searchConfiguration.filePath,
                        data.searchConfigurationPort?data.searchConfigurationPort.searchConfigurationPort:null
                    )

                    step4.loadData(data.searchConfiguration.configurationTemplate);
                }

                step5.loadData(data.searchServer);

                step1.setDisabled(false);
                step2.setDisabled(false);
                step3.setDisabled(false);
                step4.setDisabled(false);
                step5.setDisabled(false);
            },
            failure: function(error) {
           	 Ext.MessageBox.alert('Ошибка', 'Произошла ошибка: ' + error);
           }
        });

        //step1.loadDistributedData();
    },

    initComponent: function() {
        this.items = [
            {xtype: 'step1'},
            {xtype: 'step2'},
            {xtype: 'step3'},
            {xtype: 'step4'},
            {xtype: 'step5'},
            {xtype: 'step6'},
            {xtype: 'step7', finishable: true},
            {xtype: 'stepDistributed2'},//{xtype: 'stepDistributed2'}, //TODO Your naming is better ;)
            {xtype: 'stepDistributed3'},
            {xtype: 'stepDistributed4'},
            {xtype: 'stepDistributed5', finishable: true}
        ];

        this.callParent(arguments);
        this.isEdit = (this.record != null);
        if(this.isEdit) {
            if(this.record.data.collectionType == 'SIMPLE') {
                this.loadSimpleCollectionData();
            } else {
                this.loadDistributedCollectionData();
            }
        }
    },

    createDelta: function(killParameters) {
        var delta = new Object();
        delta.type = 'DELTA';
        delta.deleteScheme = killParameters;
        return delta;
    },
    
    getShpinxConfPreviewDistributedStep3Data : function() {
    	var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.stepDistributed2]');
        var step3 = this.down('panel[alias=widget.stepDistributed3]');
        
        var step1data = step1.getData();
        step1data.type = 'DISTRIBUTED';


        var collectionModel = new sphinx-console.model.Collection(step1data);
        collectionModel.set('id', null);
        
        var nodesArray = [];
        step2.getData().forEach(function (entry) {
            nodesArray.push(entry.getData(true));
        });


        //alert (Ext.JSON.encodeValue(collectionModel.getData(true), '\n'));
        
        var searchConfigurationModel = new sphinx-console.model.Configuration();
        
        searchConfigurationModel.set('id', step3.getSearchConfigurationId());
        searchConfigurationModel.set('name', step3.getSearchConfigurationName());
        searchConfigurationModel.set('filePath', step3.getSearchConfigurationFilePath());

        
        var searchConfigurationTemplateSearchModel = new sphinx-console.model.ConfigurationTemplateSearch(
                step3.getSearchСonfigurationTemplateData()
        );
        if (typeof searchConfigurationTemplateSearchModel.get('id') === 'string') {
                searchConfigurationTemplateSearchModel.set('id', null);
                searchConfigurationTemplateSearchModel.configurationFields().each(function (field){
                    field.set('id', null);
                });
        }
            
        

        searchConfigurationModel.setSearchConfigurationTemplate(searchConfigurationTemplateSearchModel);
        
        
        
        
        var distributedCollectionWrapperModel = new sphinx-console.model.DistributedCollectionWrapper({

        });
        distributedCollectionWrapperModel.setCollection(collectionModel);
        distributedCollectionWrapperModel.nodes().add(nodesArray);
        distributedCollectionWrapperModel.setSearchConfiguration(searchConfigurationModel);
        
        var searchConfigurationPort = new sphinx-console.model.SearchConfigurationPort({});
        
        
        searchConfigurationPort.set('searchConfigurationPort', step3.getSearchСonfigurationPort());
        distributedCollectionWrapperModel.setSearchConfigurationPort(searchConfigurationPort);

        
        var data = distributedCollectionWrapperModel.getAssociatedData();
        return data; 

    },
    
    getShpinxConfPreviewDistributedStep4Data : function() {
    	var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.stepDistributed2]');
        var step3 = this.down('panel[alias=widget.stepDistributed3]');
        var step4 = this.down('panel[alias=widget.stepDistributed4]');
        
    	
    	
        var step1data = step1.getData();
        step1data.type = 'DISTRIBUTED';

        var collectionModel = new sphinx-console.model.Collection(
            step1data
        );
        if (!this.isEdit) {
            collectionModel.set('id', null);
        }

        var nodesArray = [];
        step2.getData().forEach(function (entry) {
            nodesArray.push(entry.getData(true));
        });


        //alert (Ext.JSON.encodeValue(collectionModel.getData(true), '\n'));
        
        var searchConfigurationModel = new sphinx-console.model.Configuration();
        
        searchConfigurationModel.set('id', step3.getSearchConfigurationId());
        searchConfigurationModel.set('name', step3.getSearchConfigurationName());
        searchConfigurationModel.set('filePath', step3.getSearchConfigurationFilePath());

        
        var searchConfigurationTemplateSearchModel = new sphinx-console.model.ConfigurationTemplateSearch(
                step3.getSearchСonfigurationTemplateData()
        );
        if (typeof searchConfigurationTemplateSearchModel.get('id') === 'string') {
                searchConfigurationTemplateSearchModel.set('id', null);
                searchConfigurationTemplateSearchModel.configurationFields().each(function (field){
                    field.set('id', null);
                });
        }
            
        var configurationTemplateSearchModel = new sphinx-console.model.ConfigurationTemplateConfiguration(
                step4.getConfigurationTemplateConfiguration()
        );
        if (typeof configurationTemplateSearchModel.get('id') === 'string') {
                configurationTemplateSearchModel.set('id', null);
                configurationTemplateSearchModel.configurationFields().each(function (field){
                    field.set('id', null);
                });
        }

        searchConfigurationModel.setSearchConfigurationTemplate(searchConfigurationTemplateSearchModel);
        searchConfigurationModel.setConfigurationTemplate(configurationTemplateSearchModel);

        
        var distributedCollectionWrapperModel = new sphinx-console.model.DistributedCollectionWrapper({

        });
        distributedCollectionWrapperModel.setCollection(collectionModel);
        distributedCollectionWrapperModel.nodes().add(nodesArray);
        distributedCollectionWrapperModel.setSearchConfiguration(searchConfigurationModel);
        
        var searchConfigurationPort = new sphinx-console.model.SearchConfigurationPort({});
        
        
        searchConfigurationPort.set('searchConfigurationPort', step3.getSearchСonfigurationPort());
        distributedCollectionWrapperModel.setSearchConfigurationPort(searchConfigurationPort);

        
        var data = distributedCollectionWrapperModel.getAssociatedData();
        return data;
    },
    
    getShpinxConfPreviewStep2Data : function() {
        var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.step2]');
        
        var step1data = step1.getData();
        step1data.type = step2.getUseMainDeltaSchema() ? 'MAIN_DELTA' : 'SIMPLE';


        var collectionModel = new sphinx-console.model.Collection(step1data);
        collectionModel.set('id', null);
        var dataSourceIndexModel = new sphinx-console.model.Datasource(step2.getData());
        var indexConfigurationModel = new sphinx-console.model.Configuration();

        var configurationFieldsArrIndex = [];
        step2.getSourceConfigurationFieldsData().forEach(function (entry) {
            var configurationField = new sphinx-console.model.ConfigurationFields(entry);
            configurationFieldsArrIndex.push(configurationField.getData(true));

        });

        indexConfigurationModel.sourceConfigurationFields().add(configurationFieldsArrIndex);
        indexConfigurationModel.sourceConfigurationFields().each(function (configurationField){
        	configurationField.set('id', null);
        });
        
        indexConfigurationModel.set('id', null);
        
        dataSourceIndexModel.set('id', null);
        indexConfigurationModel.setDatasource(dataSourceIndexModel);

        var collectionWrapperModel = new sphinx-console.model.CollectionWrapper({});
        collectionWrapperModel.setCollection(collectionModel);
        collectionWrapperModel.setIndexConfiguration(indexConfigurationModel);
        var data = collectionWrapperModel.getAssociatedData();
        data.tableName = step2.getTableName();
        data.collection.delta = this.createDelta(step2.getKillParameters());

        return data;
    },
    
    getShpinxConfPreviewStep4Data : function(){ 
    	var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.step2]');
        var step3 = this.down('panel[alias=widget.step3]');
        var step4 = this.down('panel[alias=widget.step4]');
        
        var step1data = step1.getData();
        step1data.type = step2.getUseMainDeltaSchema() ? 'MAIN_DELTA' : 'SIMPLE';

        
    	var collectionModel = new sphinx-console.model.Collection(step1data);
        collectionModel.set('id', null);	
        var dataSourceIndexModel = new sphinx-console.model.Datasource(step2.getData());
        var indexConfigurationModel = new sphinx-console.model.Configuration(); 
        
        var configurationFieldsArrIndex = [];
        step2.getSourceConfigurationFieldsData().forEach(function(entry) {
	        var configurationField = new sphinx-console.model.ConfigurationFields(entry);
	        configurationFieldsArrIndex.push(configurationField.getData(true));   
	        
        }); 
        indexConfigurationModel.sourceConfigurationFields().add(configurationFieldsArrIndex);
        indexConfigurationModel.sourceConfigurationFields().each(function (configurationField){ 
        	configurationField.set('id', null);
        });
        
        var fieldMappingsArrIndex = [];
        step3.getData().forEach(function(entry) {
	        var fieldMapping = new sphinx-console.model.FieldMapping(entry);
	        if (fieldMapping.getData(true).indexFieldType != 'Нет') {
	            fieldMappingsArrIndex.push(fieldMapping.getData(true));   
	        }
        }); 
        indexConfigurationModel.fieldMappings().add(fieldMappingsArrIndex);
        indexConfigurationModel.set('id', null);
        indexConfigurationModel.fieldMappings().each(function (fieldMapping){ 
            fieldMapping.set('id', null);
        });
        dataSourceIndexModel.set('id', null);
        indexConfigurationModel.setDatasource(dataSourceIndexModel);
        var indexConfigurationTemplateIndexModel = new sphinx-console.model.ConfigurationTemplateIndexer(step4.getIndexerConfigurationTemplateData());
        if(indexConfigurationTemplateIndexModel.id == -1) { indexConfigurationTemplateIndexModel.set('id', null);}
        indexConfigurationTemplateIndexModel.configurationFields().each(function (field){
         	field.set('id', null);
        });
        indexConfigurationModel.setIndexerConfigurationTemplate(indexConfigurationTemplateIndexModel);
        var collectionWrapperModel = new sphinx-console.model.CollectionWrapper({});
        collectionWrapperModel.setCollection(collectionModel);
        collectionWrapperModel.setIndexConfiguration(indexConfigurationModel);
        var data = collectionWrapperModel.getAssociatedData();
        data.tableName = step2.getTableName();
        data.collection.delta = this.createDelta(step2.getKillParameters());
        return data;
    },
    getShpinxConfPreviewStep5Data : function(){ 
    	var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.step2]');
        var step3 = this.down('panel[alias=widget.step3]');
        var step4 = this.down('panel[alias=widget.step4]');
        var step5 = this.down('panel[alias=widget.step5]');
        
        var step1data = step1.getData();
        step1data.type = step2.getUseMainDeltaSchema() ? 'MAIN_DELTA' : 'SIMPLE';
        
        var collectionModel = new sphinx-console.model.Collection(step1data);
        collectionModel.set('id', null);	
        var dataSourceIndexModel = new sphinx-console.model.Datasource(step2.getData());
        var indexConfigurationModel = new sphinx-console.model.Configuration(); 
        
        var configurationFieldsArrIndex = [];
        step2.getSourceConfigurationFieldsData().forEach(function(entry) {
	        var configurationField = new sphinx-console.model.ConfigurationFields(entry);
	        configurationFieldsArrIndex.push(configurationField.getData(true));   
	        
        }); 
        indexConfigurationModel.sourceConfigurationFields().add(configurationFieldsArrIndex);
        indexConfigurationModel.sourceConfigurationFields().each(function (configurationField){ 
        	configurationField.set('id', null);
        });
        
        var fieldMappingsArrIndex = [];
            step3.getData().forEach(function(entry) {
            	var fieldMapping = new sphinx-console.model.FieldMapping(
                		entry
                );

            	if (fieldMapping.getData(true).indexFieldType != 'Нет') {
            	    fieldMappingsArrIndex.push(fieldMapping.getData(true));   
            	}
            }); 
            
         indexConfigurationModel.fieldMappings().add(fieldMappingsArrIndex);
         indexConfigurationModel.set('id', null);
         indexConfigurationModel.fieldMappings().each(function (fieldMapping){ 
                	fieldMapping.set('id', null);
                });
         dataSourceIndexModel.set('id', null);
         indexConfigurationModel.setDatasource(dataSourceIndexModel);
         var searchConfigurationTemplateIndexModel = new sphinx-console.model.ConfigurationTemplateSearch(step5.getSearchСonfigurationTemplateData());
        // searchConfigurationTemplateIndexModel.set('id', null);
         searchConfigurationTemplateIndexModel.configurationFields().each(function (field){
         	field.set('id', null);
         });
         
         var indexConfigurationTemplateIndexModel = new sphinx-console.model.ConfigurationTemplateIndexer(step4.getIndexerConfigurationTemplateData());
        if(indexConfigurationTemplateIndexModel.id == -1) { indexConfigurationTemplateIndexModel.set('id', null);}
         indexConfigurationTemplateIndexModel.configurationFields().each(function (field){
          	field.set('id', null);
         });
         indexConfigurationModel.setIndexerConfigurationTemplate(indexConfigurationTemplateIndexModel);
         indexConfigurationModel.setSearchConfigurationTemplate(searchConfigurationTemplateIndexModel);

         var searchConfigurationPort = new sphinx-console.model.SearchConfigurationPort({});
         searchConfigurationPort.set('searchConfigurationPort', Ext.getCmp('searchConfigurationPort').getValue());

         var distributedConfigurationPort = new sphinx-console.model.DistributedConfigurationPort({});
         distributedConfigurationPort.set('distributedConfigurationPort', Ext.getCmp('distributedConfigurationPort').getValue());

         
         var collectionWrapperModel = new sphinx-console.model.CollectionWrapper({});
         collectionWrapperModel.setCollection(collectionModel);
         collectionWrapperModel.setIndexConfiguration(indexConfigurationModel);
         collectionWrapperModel.setSearchConfigurationPort(searchConfigurationPort);
         collectionWrapperModel.setDistributedConfigurationPort(distributedConfigurationPort);
         var data = collectionWrapperModel.getAssociatedData();
         data.tableName = step2.getTableName();
         data.collection.delta = this.createDelta(step2.getKillParameters());
         return data;
    },
    getShpinxConfPreviewStep6Data : function(){ 
    	var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.step2]');
        var step3 = this.down('panel[alias=widget.step3]');
        var step4 = this.down('panel[alias=widget.step4]');
        var step5 = this.down('panel[alias=widget.step5]');
        var step6 = this.down('panel[alias=widget.step6]');
        
        var step1data = step1.getData();
        step1data.type = step2.getUseMainDeltaSchema() ? 'MAIN_DELTA' : 'SIMPLE';
        
        var collectionModel = new sphinx-console.model.Collection(step1data);
        collectionModel.set('id', null);	
        var dataSourceIndexModel = new sphinx-console.model.Datasource(step2.getData());
        var indexConfigurationModel = new sphinx-console.model.Configuration(); 
        
        var configurationFieldsArrIndex = [];
        step2.getSourceConfigurationFieldsData().forEach(function(entry) {
	        var configurationField = new sphinx-console.model.ConfigurationFields(entry);
	        configurationFieldsArrIndex.push(configurationField.getData(true));   
	        
        }); 
        indexConfigurationModel.sourceConfigurationFields().add(configurationFieldsArrIndex);
        indexConfigurationModel.sourceConfigurationFields().each(function (configurationField){ 
        	configurationField.set('id', null);
        });
        
        var fieldMappingsArrIndex = [];
            step3.getData().forEach(function(entry) {
            	var fieldMapping = new sphinx-console.model.FieldMapping(
                		entry
                );

            	if (fieldMapping.getData(true).indexFieldType != 'Нет') {
            	    fieldMappingsArrIndex.push(fieldMapping.getData(true));   
            	}
            }); 
            
         indexConfigurationModel.fieldMappings().add(fieldMappingsArrIndex);
         indexConfigurationModel.set('id', null);
         indexConfigurationModel.fieldMappings().each(function (fieldMapping){ 
                	fieldMapping.set('id', null);
                });
         dataSourceIndexModel.set('id', null);
         indexConfigurationModel.setDatasource(dataSourceIndexModel);
         var searchConfigurationTemplateIndexModel = new sphinx-console.model.ConfigurationTemplateSearch(step5.getSearchСonfigurationTemplateData());
        // searchConfigurationTemplateIndexModel.set('id', null);
         searchConfigurationTemplateIndexModel.configurationFields().each(function (field){
          	field.set('id', null);
         });
         var indexConfigurationTemplateIndexModel = new sphinx-console.model.ConfigurationTemplateIndexer(step4.getIndexerConfigurationTemplateData());
         if(indexConfigurationTemplateIndexModel.id == -1) { indexConfigurationTemplateIndexModel.set('id', null);}
         indexConfigurationTemplateIndexModel.configurationFields().each(function (field){
          	field.set('id', null);
         });
         var сonfigurationTemplateIndexModel = new sphinx-console.model.ConfigurationTemplateConfiguration(step6.getConfigurationTemplateConfiguration());
         indexConfigurationModel.setIndexerConfigurationTemplate(indexConfigurationTemplateIndexModel);
         indexConfigurationModel.setSearchConfigurationTemplate(searchConfigurationTemplateIndexModel);
         indexConfigurationModel.setConfigurationTemplate(сonfigurationTemplateIndexModel);
         var searchConfigurationPort = new sphinx-console.model.SearchConfigurationPort({});
         searchConfigurationPort.set('searchConfigurationPort', Ext.getCmp('searchConfigurationPort').getValue());
         
         var distributedConfigurationPort = new sphinx-console.model.DistributedConfigurationPort({});
         distributedConfigurationPort.set('distributedConfigurationPort', Ext.getCmp('distributedConfigurationPort').getValue());


         var collectionWrapperModel = new sphinx-console.model.CollectionWrapper({});
         collectionWrapperModel.setCollection(collectionModel);
         collectionWrapperModel.setIndexConfiguration(indexConfigurationModel);
         collectionWrapperModel.setSearchConfigurationPort(searchConfigurationPort);
         collectionWrapperModel.setDistributedConfigurationPort(distributedConfigurationPort);
         var data = collectionWrapperModel.getAssociatedData();
         data.tableName = step2.getTableName();
         data.collection.delta = this.createDelta(step2.getKillParameters());
         return data;
    },

    isMappingChanged: function () {
        var result = false;
        var searchConfiguration= new Object();
        var step5 = this.down('panel[alias=widget.step5]');
        searchConfiguration.id = step5.getSearchConfigurationId();
        searchConfiguration.name = step5.getSearchConfigurationName();

        var fieldMappingsArrSearch = [];
        this.down('panel[alias=widget.step3]').getData().forEach(function(entry) {
            if (entry.indexFieldType != 'Нет') {
                entry.id = null;
                fieldMappingsArrSearch.push(entry);
            }
        });
        searchConfiguration.fieldMappings = fieldMappingsArrSearch;

        Ext.Ajax.request({
            async: false,
            url: sphinx-console.util.Utilities.SERVER_URL + '/view/isMappingChanged',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            params: Ext.JSON.encodeValue(searchConfiguration, '\n'),
            method: 'POST',
            success: function(response) {
                var status = Ext.JSON.decode(response.responseText);
                result = status.result;
            },
            failure:function(response, request ) {
                Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
            }

        });
        return result;
    },

    listeners: {
        wizardcancelled: function () {
            Ext.each(this.items.items, function(step) {
                step.fireEvent('deactivate', step);
            });
            this.up('window').close();
        },

        distributedCollectionWizardFinished: function() {

            var th = this;
            
            Ext.Msg.show({
                scope: this,
                title: 'Подтверждение',
                msg: 'Проследить за ходом выполнения задания?',
                buttonText: {
                    yes: 'Да',
                    no: 'Нет'
                },
                buttons: Ext.Msg.YESNO,
                icon: Ext.Msg.QUESTION,
                fn: function (buttonId, text, opt) {
                    switch (buttonId) {
                        case 'yes':
                            var status = th.onDistributedWizardFinished(null, false);
                            var taskUid = status != null ? status.taskUID : null;
                            Ext.create('sphinx-console.view.logs.LogWindow', {uid: taskUid}).show();
                            break;
                        case 'no':
                            th.onDistributedWizardFinished();
                            break;
                    }
                }
            });

        },

        wizardfinished: function () {
            var th = this;

            var isMappingChanged = false;
            if(this.isEdit) {
                isMappingChanged = this.isMappingChanged();
            }
            if(isMappingChanged) {
                var selectServerWindow = Ext.create('sphinx-console.view.collection.wizard.SelectServerWindow');
                selectServerWindow.title = "Сервер полной \nпереиндексации";
                selectServerWindow.width = '290px';
                selectServerWindow.onSelect = function(server) {
                    var fullIndexingServer = server;
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
                                    var status = th.onWizardFinished(fullIndexingServer, false);
                                    var taskUid = status != null ? status.taskUID : null;
                                    Ext.create('sphinx-console.view.logs.LogWindow', {uid: taskUid}).show();
                                    break;
                                case 'no':
                                    th.onWizardFinished(fullIndexingServer);
                                    break;
                            }
                        }
                    });
                };
                selectServerWindow.show();
            } else {
                Ext.Msg.show({
                    scope: this,
                    title: 'Подтверждение',
                    msg: 'Проследить за ходом выполнения задания?',
                    buttonText: {
                        yes: 'Да',
                        no: 'Нет'
                    },
                    buttons: Ext.Msg.YESNO,
                    icon: Ext.Msg.QUESTION,
                    fn: function (buttonId, text, opt) {
                        switch (buttonId) {
                            case 'yes':
                                var status = th.onWizardFinished(null, false);
                                var taskUid = status != null ? status.taskUID : null;
                                Ext.create('sphinx-console.view.logs.LogWindow', {uid: taskUid}).show();
                                break;
                            case 'no':
                                th.onWizardFinished();
                                break;
                        }
                    }
                });
            }
        }
    },
    
    onDistributedWizardFinished: function (server, async_mode) {
    	if (async_mode != false) {
            async_mode = true;
        }
        var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.stepDistributed2]');
        var step3 = this.down('panel[alias=widget.stepDistributed3]');
        var step4 = this.down('panel[alias=widget.stepDistributed4]');
        var step5 = this.down('panel[alias=widget.stepDistributed5]');
        
    	
    	
        var step1data = step1.getData();
        step1data.type = 'DISTRIBUTED';

        var collectionModel = new sphinx-console.model.Collection(
            step1data
        );
        if (!this.isEdit) {
            collectionModel.set('id', null);
        }

        var nodesArray = [];
        var node;
        var nodeAgents;
        step2.getData().forEach(function (entry) {
            var result = entry.getData(true);
            result.agents.forEach(function(agent){
                delete agent.id;
            });
            nodesArray.push(result);
        });


        //alert (Ext.JSON.encodeValue(collectionModel.getData(true), '\n'));
        
        var searchConfigurationModel = new sphinx-console.model.Configuration();
        
        searchConfigurationModel.set('id', step3.getSearchConfigurationId());
        searchConfigurationModel.set('name', step3.getSearchConfigurationName());
        searchConfigurationModel.set('filePath', step3.getSearchConfigurationFilePath());

        
        var searchConfigurationTemplateSearchModel = new sphinx-console.model.ConfigurationTemplateSearch(
                step3.getSearchСonfigurationTemplateData()
        );
        if (typeof searchConfigurationTemplateSearchModel.get('id') === 'string') {
                searchConfigurationTemplateSearchModel.set('id', null);
                searchConfigurationTemplateSearchModel.configurationFields().each(function (field){
                    field.set('id', null);
                });
        }
            
        var configurationTemplateSearchModel = new sphinx-console.model.ConfigurationTemplateConfiguration(
                step4.getConfigurationTemplateConfiguration()
        );
        if (typeof configurationTemplateSearchModel.get('id') === 'string') {
                configurationTemplateSearchModel.set('id', null);
                configurationTemplateSearchModel.configurationFields().each(function (field){
                    field.set('id', null);
                });
        }

        searchConfigurationModel.setSearchConfigurationTemplate(searchConfigurationTemplateSearchModel);
        searchConfigurationModel.setConfigurationTemplate(configurationTemplateSearchModel);

        var searchServerModel = new sphinx-console.model.Server(
                step5.getSearchServerData()
        );
        
        
        var distributedCollectionWrapperModel = new sphinx-console.model.DistributedCollectionWrapper({

        });
        distributedCollectionWrapperModel.setCollection(collectionModel);
        distributedCollectionWrapperModel.nodes().add(nodesArray);
        distributedCollectionWrapperModel.setSearchServer(searchServerModel);
        distributedCollectionWrapperModel.setSearchConfiguration(searchConfigurationModel);
        
        var searchConfigurationPort = new sphinx-console.model.SearchConfigurationPort({});
        
        
        searchConfigurationPort.set('searchConfigurationPort', step3.getSearchСonfigurationPort());
        distributedCollectionWrapperModel.setSearchConfigurationPort(searchConfigurationPort);

        
        var data = distributedCollectionWrapperModel.getAssociatedData();
        if (sphinx-console.util.Utilities.DEBUG)
            console.log(Ext.JSON.encodeValue(data, '\n'));

        Ext.each(this.items.items, function(step) {
            step.fireEvent('deactivate', step);
        });
        this.up('window').close();
        
        
    	var url = sphinx-console.util.Utilities.SERVER_URL + (!this.isEdit ? '/configuration/addDistributedCollection' : '/configuration/modifyDistributedCollectionAttributes');
        var status = null;
        Ext.Ajax.request({
            //async: false,
            url:  url,
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            params: Ext.JSON.encodeValue(data, '\n'),
            waitTitle:'Connecting',
            waitMsg:'Creating...',
            method: 'POST',
            async: async_mode,
            success: function(response) {
                status = Ext.JSON.decode(response.responseText);

                if (status && status.code != 0) {
                    Ext.MessageBox.alert('Ошибка',
                            'Ошибка, интерфейс: ' + status.systemInterface +
                            ", message: " + status.message +
                            ", description: " + status.description +
                            ", StackTrace: " + status.stackTrace);
                } else {

                }

            },
            failure:function(response, request ) {
                Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
            }

        });

        return status;

    },

    onWizardFinished: function (server, async_mode) {

        if (async_mode != false) {
            async_mode = true;
        }
        var step1 = this.down('panel[alias=widget.step1]');
        var step2 = this.down('panel[alias=widget.step2]');
        var step3 = this.down('panel[alias=widget.step3]');
        var step4 = this.down('panel[alias=widget.step4]');
        var step5 = this.down('panel[alias=widget.step5]');
        var step6 = this.down('panel[alias=widget.step6]');
        var step7 = this.down('panel[alias=widget.step7]');

        var step1data = step1.getData();
        step1data.type = step2.getUseMainDeltaSchema() ? 'MAIN_DELTA' : 'SIMPLE';

        var collectionModel = new sphinx-console.model.Collection(
            step1data
        );
        if (!this.isEdit) {
            collectionModel.set('id', null);
        }

        //alert (Ext.JSON.encodeValue(collectionModel.getData(true), '\n'));

        var dataSourceSearchModel = new sphinx-console.model.Datasource(
            step2.getData()
        );
        var dataSourceIndexModel = new sphinx-console.model.Datasource(
            step2.getData()
        );

        var searchConfigurationModel = new sphinx-console.model.Configuration();
        var indexConfigurationModel = new sphinx-console.model.Configuration();

        searchConfigurationModel.set('id', step5.getSearchConfigurationId());
        searchConfigurationModel.set('name', step5.getSearchConfigurationName());
        searchConfigurationModel.set('filePath', step5.getSearchConfigurationFilePath());

        indexConfigurationModel.set('id', step4.getIndexConfigurationId());
        indexConfigurationModel.set('name', step4.getIndexConfigurationName());
        indexConfigurationModel.set('filePath', step4.getIndexConfigurationFilePath());

        var fieldMappingsArrSearch = [];
        step3.getData().forEach(function(entry) {
            var fieldMapping = new sphinx-console.model.FieldMapping(
                entry
            );

            if (fieldMapping.getData(true).indexFieldType != 'Нет') {
                fieldMappingsArrSearch.push(fieldMapping.getData(true));
            }
        });

        var fieldMappingsArrIndex = [];
        step3.getData().forEach(function(entry) {
            var fieldMapping = new sphinx-console.model.FieldMapping(
                entry
            );

            if (fieldMapping.getData(true).indexFieldType != 'Нет') {
                fieldMappingsArrIndex.push(fieldMapping.getData(true));
            }
        });

        searchConfigurationModel.fieldMappings().add(fieldMappingsArrSearch);
        indexConfigurationModel.fieldMappings().add(fieldMappingsArrIndex);
        
        
        var configurationFieldsArrSearch = [];
        step2.getSourceConfigurationFieldsData().forEach(function(entry) {
	        var configurationField = new sphinx-console.model.ConfigurationFields(entry);
	        configurationFieldsArrSearch.push(configurationField.getData(true));   
	        
        }); 
        searchConfigurationModel.sourceConfigurationFields().add(configurationFieldsArrSearch);
        searchConfigurationModel.sourceConfigurationFields().each(function (configurationField){ 
        	configurationField.set('id', null);
        });
        
        var configurationFieldsArrIndex = [];
        step2.getSourceConfigurationFieldsData().forEach(function(entry) {
	        var configurationField = new sphinx-console.model.ConfigurationFields(entry);
	        configurationFieldsArrIndex.push(configurationField.getData(true));   
	        
        }); 
        indexConfigurationModel.sourceConfigurationFields().add(configurationFieldsArrIndex);
        indexConfigurationModel.sourceConfigurationFields().each(function (configurationField){ 
        	configurationField.set('id', null);
        });

         if (dataSourceSearchModel.get('id') < 0) {
             dataSourceSearchModel.set('id', null);
         }
         if (dataSourceIndexModel.get('id') < 0) {
             dataSourceIndexModel.set('id', null);
         }

        searchConfigurationModel.fieldMappings().each(function (fieldMapping){
            fieldMapping.set('id', null);
        });
        indexConfigurationModel.fieldMappings().each(function (fieldMapping){
            fieldMapping.set('id', null);
        });

        searchConfigurationModel.setDatasource(dataSourceSearchModel);
        indexConfigurationModel.setDatasource(dataSourceIndexModel);

        var searchConfigurationTemplateSearchModel = new sphinx-console.model.ConfigurationTemplateSearch(
            step5.getSearchСonfigurationTemplateData()
        );
        if (typeof searchConfigurationTemplateSearchModel.get('id') === 'string') {
            searchConfigurationTemplateSearchModel.set('id', null);
            searchConfigurationTemplateSearchModel.configurationFields().each(function (field){
                field.set('id', null);
            });
        }
        var indexConfigurationTemplateSearchModel = new sphinx-console.model.ConfigurationTemplateIndexer(
            step4.getIndexerConfigurationTemplateData()
        );
        if (typeof indexConfigurationTemplateSearchModel.get('id') === 'string') {
            indexConfigurationTemplateSearchModel.set('id', null);
            indexConfigurationTemplateSearchModel.configurationFields().each(function (field){
                field.set('id', null);
            });
        }
        var configurationTemplateSearchModel = new sphinx-console.model.ConfigurationTemplateConfiguration(
            step6.getConfigurationTemplateConfiguration()
        );
        if (typeof configurationTemplateSearchModel.get('id') === 'string') {
            configurationTemplateSearchModel.set('id', null);
            configurationTemplateSearchModel.configurationFields().each(function (field){
                field.set('id', null);
            });
        }

        var searchConfigurationTemplateIndexModel = new sphinx-console.model.ConfigurationTemplateSearch(
            step5.getSearchСonfigurationTemplateData()
        );
        if (typeof searchConfigurationTemplateIndexModel.get('id') === 'string') {
            searchConfigurationTemplateIndexModel.set('id', null);
            searchConfigurationTemplateIndexModel.configurationFields().each(function (field){
                field.set('id', null);
            });
        }
        var indexConfigurationTemplateIndexModel = new sphinx-console.model.ConfigurationTemplateIndexer(
            step4.getIndexerConfigurationTemplateData()
        );
        if (typeof indexConfigurationTemplateIndexModel.get('id') === 'string') {
            indexConfigurationTemplateIndexModel.set('id', null);
            indexConfigurationTemplateIndexModel.configurationFields().each(function (field){
                field.set('id', null);
            });
        }
        var  configurationTemplateIndexModel = new sphinx-console.model.ConfigurationTemplateConfiguration(
            step6.getConfigurationTemplateConfiguration()
        );
        if (typeof configurationTemplateIndexModel.get('id') === 'string') {
            configurationTemplateIndexModel.set('id', null);
            configurationTemplateIndexModel.configurationFields().each(function (field){
                field.set('id', null);
            });
        }

        searchConfigurationModel.setIndexerConfigurationTemplate(indexConfigurationTemplateSearchModel);
        searchConfigurationModel.setSearchConfigurationTemplate(searchConfigurationTemplateSearchModel);
        searchConfigurationModel.setConfigurationTemplate(configurationTemplateSearchModel);


        indexConfigurationModel.setIndexerConfigurationTemplate(indexConfigurationTemplateIndexModel);
        indexConfigurationModel.setSearchConfigurationTemplate(searchConfigurationTemplateIndexModel);
        indexConfigurationModel.setConfigurationTemplate(configurationTemplateIndexModel);


        var cronScheduleWrapperModel = new sphinx-console.model.CronScheduleWrapper({});
        cronScheduleWrapperModel.set('cronSchedule', step7.createCronExpression());

        var cronDeltaScheduleWrapperModel = new sphinx-console.model.CronScheduleWrapper({});
        cronDeltaScheduleWrapperModel.set('cronSchedule', step7.createCronDeltaExpression());

        var searchServerModel = new sphinx-console.model.Server(
            step7.getSearchServerData()
        );

        var indexServerModel = new sphinx-console.model.Server(
            step7.getIndexServerData()
        );

        var collectionWrapperModel = new sphinx-console.model.CollectionWrapper({

        });
        collectionWrapperModel.setCronSchedule(cronScheduleWrapperModel);
        collectionWrapperModel.setMainCronSchedule(cronDeltaScheduleWrapperModel);
        collectionWrapperModel.setCollection(collectionModel);
        collectionWrapperModel.setSearchServer(searchServerModel);
        collectionWrapperModel.setIndexServer(indexServerModel);
        collectionWrapperModel.setSearchConfiguration(searchConfigurationModel);
        collectionWrapperModel.setIndexConfiguration(indexConfigurationModel);

        var searchConfigurationPort = new sphinx-console.model.SearchConfigurationPort({});
        searchConfigurationPort.set('searchConfigurationPort', Ext.getCmp('searchConfigurationPort').getValue());
        collectionWrapperModel.setSearchConfigurationPort(searchConfigurationPort);
        
        var distributedConfigurationPort = new sphinx-console.model.DistributedConfigurationPort({});
        distributedConfigurationPort.set('distributedConfigurationPort', Ext.getCmp('distributedConfigurationPort').getValue());
        collectionWrapperModel.setDistributedConfigurationPort(distributedConfigurationPort);


        var delta = step1.getDelta();
        delta.type ='DELTA';

        var externalAction = step7.getExternalAction();
        delta.externalAction = externalAction;
        delta.deleteScheme = step2.getKillParameters();

        var data = collectionWrapperModel.getAssociatedData();
        if (sphinx-console.util.Utilities.DEBUG)
            console.log(Ext.JSON.encodeValue(data, '\n'));

        Ext.each(this.items.items, function(step) {
            step.fireEvent('deactivate', step);
        });
        this.up('window').close();
        data.tableName = step2.getTableName();
        data.collection.delta = delta;
        data.fullIndexingServer = server;
        var url = sphinx-console.util.Utilities.SERVER_URL + (!this.isEdit ? '/configuration/addCollection' : '/configuration/modifyCollectionAttributes');
        var status = null;
        Ext.Ajax.request({
            //async: false,
            url:  url,
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            params: Ext.JSON.encodeValue(data, '\n'),
            waitTitle:'Connecting',
            waitMsg:'Creating...',
            method: 'POST',
            async: async_mode,
            success: function(response) {
                status = Ext.JSON.decode(response.responseText);

                if (status && status.code != 0) {
                    Ext.MessageBox.alert('Ошибка',
                            'Ошибка, интерфейс: ' + status.systemInterface +
                            ", message: " + status.message +
                            ", description: " + status.description +
                            ", StackTrace: " + status.stackTrace);
                } else {

                }

            },
            failure:function(response, request ) {
                Ext.MessageBox.alert('Ошибка', 'Произошла ошибка');
            }

        });

        return status;
    }
});




