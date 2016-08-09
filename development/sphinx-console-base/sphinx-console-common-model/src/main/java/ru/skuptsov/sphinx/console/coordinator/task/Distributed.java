package ru.skuptsov.sphinx.console.coordinator.task;

import java.util.List;
import java.util.Set;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.DistributedCollectionNode;

public interface Distributed {
    Set<DistributedCollectionNode> getNodes();
    void addNode(DistributedCollectionNode node);
    void setNodes(Set<DistributedCollectionNode> nodes);
    List<String> getAgentConfigs();
    Collection getCollection();
    Configuration getSearchConfiguration();
    Integer getSearchConfigurationPort();
    String getProcessName();
}
