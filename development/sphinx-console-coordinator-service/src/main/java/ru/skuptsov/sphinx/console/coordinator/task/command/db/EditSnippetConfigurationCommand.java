package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.EditSnippetConfigurationTask;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.RebuildSnippetTaskScheduler;
import ru.skuptsov.sphinx.console.spring.service.api.SnippetConfigurationService;

@Component("EDIT_SNIPPET_CONFIGURATION")
@Scope("prototype")
public class EditSnippetConfigurationCommand extends DbCommand<EditSnippetConfigurationTask> {
	 @Autowired
	 private SnippetConfigurationService snippetConfigurationService;
	 
	 @Autowired
     private RebuildSnippetTaskScheduler rebuildSnippetTaskScheduler;

	 @Override
	 @SaveActivityLog
	 public Status execute(EditSnippetConfigurationTask task) {
	     logger.debug("EDIT_SNIPPET_CONFIGURATION STATE EXECUTION...");

	     Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());
	     
	     rebuildSnippetTaskScheduler.disableScheduling(task.getSnippetConfiguration().getScheduledTask());

	     snippetConfigurationService.save(task.getSnippetConfiguration());
	     
	     rebuildSnippetTaskScheduler.enableScheduling(task.getSnippetConfiguration().getScheduledTask());

	     return status;
	 }	
	
}

