Ext.define('sphinx-console.view.collection.wizard.DistributedCollectionNodesPanel' ,{
    extend: 'Ext.form.Panel',
    alias : 'widget.distributedCollectionNodesPanel',
//    autoScroll: true,
    simpleCollectionsStore: null,
    resultStore: null,

    rebuildFromData: function(distributedCollectionNodesData){
        var th = this;
        th.removeAll();
        var agentsPanel;
        for(var i = 0; i < distributedCollectionNodesData.length; i++){
            agentsPanel = this.addAgentsPanel(distributedCollectionNodesData[i].collectionName, distributedCollectionNodesData[i].agents);
        }

    },

    addAgentsPanel: function(collectionName, agents){
        var agentsPanel;
        agentsPanel = Ext.create('sphinx-console.view.collection.wizard.DistributedCollectionAgentsPanel',
            {
                collectionName: collectionName,
                agentsData: agents});
        var allAgentPanels = this.query('distributedCollectionAgentsPanel');
        var prevAgentPanelCollectionName = null;
        var agentPanelCollectionName;
        for(var i = 0; i < allAgentPanels.length; i++){
            agentPanelCollectionName = allAgentPanels[i].collectionName;
            if(collectionName < agentPanelCollectionName && (prevAgentPanelCollectionName == null || collectionName > prevAgentPanelCollectionName)){
                break;
            }
            else{
                prevAgentPanelCollectionName = agentPanelCollectionName;
            }
        }
        this.insert(i, agentsPanel);
        this.resultStore.add(this.simpleCollectionsStore.findRecord('collectionName', collectionName))
    }

});