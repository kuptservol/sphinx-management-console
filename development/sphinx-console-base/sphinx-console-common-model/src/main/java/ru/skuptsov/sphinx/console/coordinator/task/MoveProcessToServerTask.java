package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.Configuration;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 18.08.14
 * Time: 0:31
 * To change this template use File | Settings | File Templates.
 */
public class MoveProcessToServerTask extends ProcessTask {
    public static final TaskName TASK_NAME = TaskName.MOVE_PROCESS_TO_SERVER;
    private static final Chain CHAIN = Chain.MOVE_PROCESS_TO_SERVER_CHAIN;

    private Configuration searchConfiguration;
    private Configuration indexConfiguration;
    private String collectionName;
    private String cronSchedule;
    private String oldServerName;
    private String mergeDeltaCronSchedule;


    public MoveProcessToServerTask() {
        super();
        setSphinxProcessType(SphinxProcessType.SEARCHING);
    }

    @Override
    public TaskName getTaskName() {
        return TASK_NAME;
    }

    public Configuration getSearchConfiguration() {
        return searchConfiguration;
    }

    @Override
    public Configuration getIndexConfiguration() {
        return indexConfiguration;
    }

    public void setSearchConfiguration(Configuration searchConfiguration) {
        this.searchConfiguration = searchConfiguration;
    }

    public void setIndexConfiguration(Configuration indexConfiguration) {
        this.indexConfiguration = indexConfiguration;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public String getCronSchedule() {
        return cronSchedule;
    }

    public String getOldServerName() {
        return oldServerName;
    }

    public void setOldServerName(String oldServerName) {
        this.oldServerName = oldServerName;
    }
    
	@Override
    public Chain getChain() {
        return CHAIN;
    }
}
