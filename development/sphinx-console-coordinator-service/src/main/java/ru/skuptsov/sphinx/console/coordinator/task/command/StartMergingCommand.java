package ru.skuptsov.sphinx.console.coordinator.task.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.DeleteScheme;
import ru.skuptsov.sphinx.console.coordinator.model.DeleteSchemeType;
import ru.skuptsov.sphinx.console.coordinator.model.Delta;
import ru.skuptsov.sphinx.console.coordinator.model.Task.MergeOption;
import ru.skuptsov.sphinx.console.coordinator.task.IndexNameTask;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

@Component("START_MERGING")
@Scope("prototype")
public class StartMergingCommand<T extends IndexNameTask> extends AgentCommand<T> implements AsyncCommand {
	@Autowired
    private CollectionService collectionService;

    public static final String AGENT_REMOTE_METHOD_NAME = "startMerging";

    @Override
    public AgentRemoteMethod getAgentRemoteMethod(IndexNameTask task) throws NoSuchMethodException {
        Object[] arguments = {task};
        return new AgentRemoteMethod(AGENT_REMOTE_CLASS.getMethod(AGENT_REMOTE_METHOD_NAME, Task.class), arguments);
    }

    @Override
    public void executeBeforeAgentRemoteMethod(IndexNameTask task) {
        logger.debug("START_MERGING " + task.getSphinxProcessType().name() + " EXECUTION...");
        
        Collection collection = collectionService.getCollection(task.getCollectionName());
        
        logger.info("RETRIEVED COLLECTION: " + collection);
        
        Delta delta = collection.getDelta();
        logger.info("RETRIEVED DELTA: " + delta);
        if (delta != null) {
        	DeleteScheme deleteScheme = delta.getDeleteScheme();
        	logger.info("RETRIEVED DELETE SCHEME: " + deleteScheme);
        	
        	if (deleteScheme != null) {
        		MergeOption mergeOption = new MergeOption();
        		
        		if (deleteScheme.getType() == DeleteSchemeType.BUSINESS_FIELD) {
        			mergeOption.setFieldName(deleteScheme.getFieldKey());
        			mergeOption.setFieldValueFrom(deleteScheme.getFieldValueFrom());
                    mergeOption.setFieldValueTo(deleteScheme.getFieldValueTo());
                }
        		
        		task.setMergeOption(mergeOption);
        	}
        }
        
    }

    @Override
    public String getAgentAddress(IndexNameTask task){
        return task.getIndexAgentAddress();
    }

}
