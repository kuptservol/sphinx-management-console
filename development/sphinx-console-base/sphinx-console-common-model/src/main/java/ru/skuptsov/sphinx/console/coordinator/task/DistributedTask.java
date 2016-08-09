package ru.skuptsov.sphinx.console.coordinator.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.skuptsov.sphinx.console.coordinator.model.DistributedCollectionAgent;
import ru.skuptsov.sphinx.console.coordinator.model.DistributedCollectionNode;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public abstract class DistributedTask extends ProcessTask implements Distributed {
	protected Set<DistributedCollectionNode> nodes = new HashSet<DistributedCollectionNode>();
	
	@Override
	public Set<DistributedCollectionNode> getNodes() {
		return nodes;
	}

	@Override
	public void addNode(DistributedCollectionNode node) {
		if (node != null) {
			nodes.add(node);
		}
	}

	@Override
	public void setNodes(Set<DistributedCollectionNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public List<String> getAgentConfigs() {
		List<String> configs = new ArrayList<String>();
		
		if (nodes != null) {
			for (DistributedCollectionNode  node : nodes) {
				StringBuilder config = new StringBuilder();
				
				Set<DistributedCollectionAgent> agents = node.getDistributedCollectionAgents();
			    
			    for (DistributedCollectionAgent agent : agents) {
			    	config.append(agent.getNodeHost()).append(":").append(agent.getNodeDistribPort()).append("|");
			    }
			    int index = config.toString().lastIndexOf("|");
                if(index >= 0) {
                    config.deleteCharAt(index);
                    config.append(":").append(node.getSimpleCollection().getName());
                }
			    
			    configs.add(config.toString());
			}
		}
		
		return configs;
	}
}
