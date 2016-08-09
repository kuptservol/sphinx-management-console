package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

public class FullRebuildSnippetTask extends RebuildSnippetsTask {
	public static final TaskName TASK_NAME = TaskName.FULL_REBUILD_SNIPPET;
    private static final Chain CHAIN = Chain.FULL_REBUILD_SNIPPET_CHAIN;
    
    public FullRebuildSnippetTask() {
        super();
    	this.setSphinxProcessType(SphinxProcessType.INDEXING);
    	this.setSnippetFullRebuild(true);
    }
    
	@Override
	public TaskName getTaskName() {
		return TASK_NAME;
	}
	
	@Override
	public Chain getChain() {
	    return CHAIN;
	}
}
