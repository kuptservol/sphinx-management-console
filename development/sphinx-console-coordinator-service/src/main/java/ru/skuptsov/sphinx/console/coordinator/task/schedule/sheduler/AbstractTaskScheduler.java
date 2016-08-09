package ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.model.Collection;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTask;
import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.model.SnippetConfiguration;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.service.AbstractScheduleService;
import ru.skuptsov.sphinx.console.spring.service.api.CollectionService;
import ru.skuptsov.sphinx.console.spring.service.api.ScheduledTaskService;
import ru.skuptsov.sphinx.console.spring.service.api.SnippetConfigurationService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public abstract class AbstractTaskScheduler {

	private static final Logger logger = LoggerFactory.getLogger(AbstractTaskScheduler.class);
    // задержка, добавляемая к следующему валидному времени запуска таска,
    // чтобы была возможность добавить/изменить триггер и успеть поставить его на паузу
    private static final int START_STOP_DELAY = 2000;

    protected abstract String getGroupName();
    protected abstract AbstractScheduleService getScheduleService();
    protected abstract ScheduledTaskType getScheduledTaskType();


	@Autowired
	protected Scheduler scheduler;
	
	@Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private SnippetConfigurationService snippetConfigurationService;
	
	public void deleteScheduler(ScheduledTask scheduledTask) {
        String collectionName = scheduledTask.getCollection().getName();
        String schedulerName = getSchedulerName(collectionName);
		logger.info("ABOUT TO DELETE SCHEDULER " + schedulerName);
	    try {
			scheduler.deleteJob(schedulerName, getGroupName());
            scheduledTaskService.delete(scheduledTask);
		} catch (SchedulerException e) {
            throw new ApplicationException("ERROR OCCURED WHILE DELETING SCHEDULER " + schedulerName, e);
		}
	}

    public void disableScheduling(ScheduledTask scheduledTask) {
        String collectionName = scheduledTask.getCollection().getName();
        String schedulerName = getSchedulerName(collectionName);
        logger.info("ABOUT TO DISABLE SCHEDULER " + schedulerName);
        try {
            CronTriggerBean cronTrigger = (CronTriggerBean) scheduler.getTrigger(schedulerName, getGroupName());

            logger.info("GOT CRON TRIGGER: " + cronTrigger);

            if(cronTrigger == null)
            {
                throw new ApplicationException("SCHEDULER "+ schedulerName +" DOESN'T EXISTS");
            }

            scheduler.pauseJob(schedulerName, getGroupName());
            scheduledTask.setIsEnabled(false);
            scheduledTaskService.save(scheduledTask);
            updateNextIndexingTime(collectionName, null);
            logger.info("DISABLE SCHEDULER " + schedulerName + " SUCCEED");

        } catch (SchedulerException e) {
            throw new ApplicationException("ERROR OCCURED WHILE ENABLING SCHEDULER " + schedulerName, e);
        }
    }

    public void enableScheduling(ScheduledTask scheduledTask) {
        String collectionName = scheduledTask.getCollection().getName();
        String schedulerName = getSchedulerName(collectionName);
        logger.info("ABOUT TO ENABLE SCHEDULER " + schedulerName);
        try {
            CronTriggerBean cronTrigger = (CronTriggerBean) scheduler.getTrigger(schedulerName, getGroupName());

            logger.info("GOT CRON TRIGGER: " + cronTrigger);

            if(cronTrigger == null)
            {
            	logger.error("SCHEDULER " + schedulerName + " DOESN'T EXISTS, TRY TO ADD NEW");
                return;
            }

            scheduler.resumeJob(schedulerName, getGroupName());
            scheduledTask.setIsEnabled(true);
            scheduledTaskService.save(scheduledTask);
            updateNextIndexingTime(collectionName, scheduledTask.getCronSchedule());
            logger.info("ENABLE SCHEDULER " + schedulerName+ " SUCCEED");

        } catch (SchedulerException e) {
            throw new ApplicationException("ERROR OCCURED WHILE ENABLING SCHEDULER " + schedulerName, e);
        }
    }

	public void reSchedule(ScheduledTask scheduledTask, String newCronExcepression) {
        String collectionName = scheduledTask.getCollection().getName();
        String schedulerName = getSchedulerName(collectionName);
		logger.info("ABOUT TO RE-SCHEDULER " + schedulerName + ", NEW CRON EXPRESSION: " + newCronExcepression);
		try {

			CronTriggerBean cronTrigger = (CronTriggerBean) scheduler.getTrigger(schedulerName, getGroupName());
            boolean triggerIsPaused = scheduler.getTriggerState(schedulerName, getGroupName()) == 1;

			logger.info("GOT CRON TRIGGER: " + cronTrigger);
			
			org.quartz.CronExpression parser = new org.quartz.CronExpression(newCronExcepression);

            // добавляем задержку START_STOP_DELAY миллисекунд в связи с тем, что нельзя изменить расписание шедулера и оставить его неактивным(если меняется выключенное расписание).
            // оно включается автоматом при rescheduleJob. чтобы успеть поставить задание на паузу после rescheduleJob, нужна задержка
			Date nextFireTime = parser.getNextValidTimeAfter(new Date(new Date().getTime() + START_STOP_DELAY));
			logger.info("NEXT FIRE TIME: " + nextFireTime);

			if (cronTrigger != null) {
				cronTrigger.setCronExpression(newCronExcepression);
				cronTrigger.setStartTime(nextFireTime);
		        cronTrigger.afterPropertiesSet();
		        logger.info("CRON TRIGGER, FOR THE JOB:" + cronTrigger.getJobName() + ", JOB GROUP: " + cronTrigger.getJobGroup());
			} else {
                logger.error(MessageFormat.format("Can't find trigger {0} with group {1}. Can't update crone expression", schedulerName, getGroupName()));
			}

	        scheduler.rescheduleJob(schedulerName, getGroupName(), cronTrigger);

            scheduledTask.setCronSchedule(newCronExcepression);
            scheduledTaskService.save(scheduledTask);

            if(triggerIsPaused){
                scheduler.pauseJob(schedulerName, getGroupName());
            }
            else{
                updateNextIndexingTime(collectionName, newCronExcepression);
            }

		} catch (Throwable e) {
            throw new ApplicationException("ERROR OCCURED WHILE ENABLING SCHEDULER " + schedulerName, e);
		}
	}

    public void addScheduler(ScheduledTask scheduledTask) {
        String collectionName = scheduledTask.getCollection().getName();
        String schedulerName = getSchedulerName(collectionName);
        String cronExcepression = scheduledTask.getCronSchedule();
        AbstractScheduleService scheduleService = getScheduleService();
        logger.info("SCHEDULE SERVICE INSTANCE: " + scheduleService);
        scheduleService.setCollectionName(scheduledTask.getCollection().getName());

        try {
            CronTriggerBean trigger = (CronTriggerBean) scheduler.getTrigger(schedulerName, getGroupName());
            logger.info("GOT CRON TRIGGER: " + trigger);

            if (trigger != null) {
                reSchedule(scheduledTask, scheduledTask.getCronSchedule());
            } else {
                // create JOB
                MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
                jobDetail.setTargetObject(scheduleService);
                jobDetail.setTargetMethod("execute");
                jobDetail.setGroup(getGroupName());
                jobDetail.setName(schedulerName);
                jobDetail.setConcurrent(false);
                jobDetail.afterPropertiesSet();

                // create CRON Trigger
                CronTriggerBean cronTrigger = new CronTriggerBean();
                cronTrigger.setBeanName(schedulerName + "-CRON");
                cronTrigger.setName(schedulerName);
                cronTrigger.setGroup(getGroupName());
                //
                logger.info("REBUILD SNIPPET SCHEDULE cronExcepression - " + cronExcepression);
                cronTrigger.setCronExpression(cronExcepression);
                cronTrigger.setMisfireInstruction(CronTriggerBean.MISFIRE_INSTRUCTION_DO_NOTHING);
                cronTrigger.afterPropertiesSet();

                scheduler.scheduleJob((JobDetail) jobDetail.getObject(), cronTrigger);
                scheduledTaskService.save(scheduledTask);
                if(!scheduledTask.getIsEnabled()){
                    scheduler.pauseJob(schedulerName, getGroupName());
                }
                updateNextIndexingTime(collectionName, scheduledTask.getCronSchedule());

            }


        } catch (Exception e) {
            throw new ApplicationException("error occured while creating scheduler", e);
        }

    }

	@PostConstruct
	public void initIt() throws Exception {
	    logger.info("ABOUT TO INIT RebuildSnippetTaskScheduler...");

	    List<ScheduledTask> tasks = scheduledTaskService.getScheduledTasks(getScheduledTaskType());

	    logger.info("RETRIEVED SCHEDULED TASKS: " + tasks);

	    if (tasks != null && tasks.size() > 0) {
	    	for (ScheduledTask task : tasks) {
                addScheduler(task);
	    	}
	    }

        // Start Scheduler
        scheduler.start();
	}
 
	@PreDestroy
	public void cleanUp() throws Exception {
		logger.info("ABOUT TO DESTROY RebuildSnippetTaskScheduler..."); 
	}

    private void updateNextIndexingTime(String collectionName, String cronExcepression) {
        Date nextFireTime = null;
        if(cronExcepression != null){
            org.quartz.CronExpression parser = null;
            try {
                parser = new org.quartz.CronExpression(cronExcepression);
                nextFireTime = parser.getNextValidTimeAfter(new Date());
            } catch (ParseException e) {
                logger.error("Can't update next indexing time, invalid cron expression: " + cronExcepression);
            }
        }
        switch (getScheduledTaskType()){
            case BUILD_SNIPPET:{
                SnippetConfiguration snippetConfiguration = snippetConfigurationService.getSnippet(collectionName);
                if(snippetConfiguration != null){
                    snippetConfiguration.setNextBuildSnippet(nextFireTime);
                    snippetConfigurationService.save(snippetConfiguration);
                }
                break;
            }
            case INDEXING_DELTA:{
                Collection collection = collectionService.getCollection(collectionName);
                collection.setNextIndexingTime(nextFireTime);
                collectionService.save(collection);
                break;
            }
            case MERGE_DELTA:{
                Collection collection = collectionService.getCollection(collectionName);
                collection.setNextMergeTime(nextFireTime);
                collectionService.save(collection);
                break;
            }
        }

    }

    private String getSchedulerName(String collectionName){
        return getGroupName() + "-" + collectionName;
    }
}