package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 12.08.14
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class DeleteCollectionTask extends ReplicaLoopTask {
    public static final TaskName TASK_NAME = TaskName.DELETE_COLLECTION;
    private static final Chain CHAIN = Chain.DELETE_COLLECTION_CHAIN;
    
    private boolean dbPartOnly = false;

    public DeleteCollectionTask() {
        super();
        this.setSphinxProcessType(SphinxProcessType.SEARCHING);
    }

    @Override
    public String getCollectionName() {
    	if (getCollection() != null) {
            return getCollection().getName();
    	}
    	
    	throw new ApplicationException("collection is empty.");
    }

    @Override
    public TaskName getTaskName() {
        return TASK_NAME;
    }

    @Override
    public Chain getChain() {
    	if (dbPartOnly) {
    		return Chain.DELETE_COLLECTION_DB_PART_CHAIN;
    	}
        return CHAIN;
    }

	public boolean isDbPartOnly() {
		return dbPartOnly;
	}

	public void setDbPartOnly(boolean dbPartOnly) {
		this.dbPartOnly = dbPartOnly;
	}
    
    
}
