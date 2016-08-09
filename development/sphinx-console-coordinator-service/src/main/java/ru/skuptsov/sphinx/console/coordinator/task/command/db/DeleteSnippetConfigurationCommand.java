package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.ReplicaLoopTask;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.RebuildSnippetTaskScheduler;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.SnippetConfigurationService;

@Component("DELETE_SNIPPET_CONFIGURATION")
@Scope("prototype")
public class DeleteSnippetConfigurationCommand extends DbCommand<ReplicaLoopTask> {
	 @Autowired
	 private SnippetConfigurationService snippetConfigurationService;
	 
	 @Autowired
     private RebuildSnippetTaskScheduler rebuildSnippetTaskScheduler;
	 
	 @Autowired
	 private CollectionService collectionService;

	 @Override
	 @SaveActivityLog
	 public Status execute(ReplicaLoopTask task) {
	     logger.debug("DELETE_SNIPPET_CONFIGURATION STATE EXECUTION...");

	     Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
	     
	     Collection collection = collectionService.getCollection(task.getCollectionName());
	     SnippetConfiguration snippetConfiguration = collection.getSnippetConfiguration();
	     
	     if (snippetConfiguration != null) {
	    	 ScheduledTask scheduledTask = snippetConfiguration.getScheduledTask();

			 logger.info("ABOUT TO DELETE SCHEDULED TASK FOR SNIPPET, WITH ID: " + scheduledTask.getId());
			 rebuildSnippetTaskScheduler.deleteScheduler(scheduledTask);

	    	 snippetConfigurationService.delete(snippetConfiguration);
	    	 
	     }
	     
	     return status;
	 }	
}
