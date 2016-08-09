package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.annotation.SaveActivityLog;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.task.CreateSnippetConfigurationTask;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler.RebuildSnippetTaskScheduler;
import ru.skuptsov.sphinx.console.spring.service.api.SnippetConfigurationService;

@Component("CREATE_SNIPPET_CONFIGURATION")
@Scope("prototype")
public class CreateSnippetConfigurationCommand extends DbCommand<CreateSnippetConfigurationTask> {
	 @Autowired
	 private SnippetConfigurationService snippetConfigurationService;
	 
	 @Autowired
     private RebuildSnippetTaskScheduler rebuildSnippetTaskScheduler;

	 @Override
	 @SaveActivityLog
	 public Status execute(CreateSnippetConfigurationTask task) {
	     logger.debug("CREATE_SNIPPET_CONFIGURATION STATE EXECUTION...");

	     Status status = Status.build(Status.SystemInterface.COORDINATOR_DB, Status.StatusCode.SUCCESS_CODE, task.getTaskUID());

		 ScheduledTask scheduledTask = task.getSnippetConfiguration().getScheduledTask();
		 scheduledTask.setIsEnabled(false);
		 rebuildSnippetTaskScheduler.addScheduler(scheduledTask);

	     snippetConfigurationService.save(task.getSnippetConfiguration());

		 rebuildSnippetTaskScheduler.enableScheduling(scheduledTask);
	     return status;
	 }	
	
}
