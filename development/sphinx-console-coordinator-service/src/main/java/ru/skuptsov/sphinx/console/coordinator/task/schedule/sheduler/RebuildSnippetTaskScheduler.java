package ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.service.AbstractScheduleService;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.service.RebuildSnippetScheduleService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

@Component
public class RebuildSnippetTaskScheduler extends AbstractTaskScheduler{
	private static final Logger logger = LoggerFactory.getLogger(RebuildSnippetTaskScheduler.class);

    @Override
    public String getGroupName(){
        return "COLLECTION-SNIPPET";
    }

    @Override
    protected AbstractScheduleService getScheduleService() {
        return (RebuildSnippetScheduleService)ApplicationContextProvider.getBean("rebuildSnippetScheduleService");
    }

    @Override
    protected ScheduledTaskType getScheduledTaskType() {
        return ScheduledTaskType.BUILD_SNIPPET;
    }

    @Override
	public void deleteScheduler(ScheduledTask scheduledTask) {
        String collectionName = scheduledTask.getCollection().getName();
        String schedulerName = getGroupName() + collectionName;
        logger.info("ABOUT TO DELETE SCHEDULER " + schedulerName);
	    try {
			scheduler.deleteJob(getGroupName() + "-" + collectionName, getGroupName());
		} catch (SchedulerException e) {
            throw new ApplicationException("ERROR OCCURED WHILE DELETING SCHEDULER " + schedulerName, e);
		}
	}

}