package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.Server;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcess;

public interface MakeCollectionFullRebuildTask extends FullIndexNameTask {

    void setCollection(Collection collection);
    void setIndexServer(Server server);
    void setIndexConfiguration(Configuration configuration);
    void setSearchConfiguration(Configuration configuration);
    void setSearchConfigurationPort(Integer configurationPort);
    void setDistributedConfigurationPort(Integer configurationPort);
    void setMainSqlQuery(String mainSqlQuery);
    void setDeltaSqlQuery(String deltaSqlQuery);
    void setCronSchedule(String cronSchedule);
    void setSphinxProcess(SphinxProcess sphinxProcess);
}
