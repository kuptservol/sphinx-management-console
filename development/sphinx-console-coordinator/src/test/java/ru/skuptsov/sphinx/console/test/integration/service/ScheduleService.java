package ru.skuptsov.sphinx.console.test.integration.service;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorConfigurationRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.task.MergeCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildCollectionTask;
import ru.skuptsov.sphinx.console.coordinator.task.RebuildSnippetsTask;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lnovikova on 18.08.2015.
 */
@Service
public class ScheduleService{

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    ServiceUtils serviceUtils;

    @Autowired
    TestExecutor testExecutor;

    @Autowired
    TaskService taskService;

    public void changeSchedule(String collectionName, String cronExpression, ScheduledTaskType type) throws InterruptedException {
        UpdateScheduleWrapper scheduleWrapper = new UpdateScheduleWrapper();
        scheduleWrapper.setCollectionName(collectionName);
        scheduleWrapper.setCronExpression(cronExpression);
        scheduleWrapper.setType(type);

        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + CoordinatorConfigurationRestURIConstants.CHANGE_COLLECTION_UPDATE_SCHEDULE, scheduleWrapper, Status.class);
        Assert.assertEquals(0, status.getCode());

        String realCroneExpression = null;
        if(type == ScheduledTaskType.BUILD_SNIPPET){
            SnippetConfigurationWrapper snippetConfigurationWrapper = testExecutor.getSnippetConfigurationWrapper(collectionName);
            realCroneExpression = snippetConfigurationWrapper.getCron().getCronSchedule();
        }
        else{
            CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(collectionName);
            realCroneExpression = type == ScheduledTaskType.MERGE_DELTA ? collectionWrapper.getMainCronSchedule().getCronSchedule() : collectionWrapper.getCronSchedule().getCronSchedule();
        }
        Assert.assertEquals(cronExpression, realCroneExpression);
    }

    public void changeEnabledSchedule(String collectionName, String cronExpression, ScheduledTaskType type, int repeatIntervalSec) throws InterruptedException {
        changeSchedule(collectionName, cronExpression, type);
        scheduleTasksRunning(collectionName, type, repeatIntervalSec);
    }

    public void changeDisabledSchedule(String collectionName, String cronExpression, ScheduledTaskType type, int repeatIntervalSec) throws InterruptedException {
        changeSchedule(collectionName, cronExpression, type);
        scheduleTasksNotRunning(collectionName, type, repeatIntervalSec);
    }

    public TaskName getTaskName(ScheduledTaskType type){
        TaskName taskName = null;
        switch (type){
            case BUILD_SNIPPET:
            {
                taskName = RebuildSnippetsTask.TASK_NAME;
                break;
            }
            case INDEXING_DELTA:
            {
                taskName = RebuildCollectionTask.TASK_NAME;
                break;
            }
            case MERGE_DELTA:
            {
                taskName = MergeCollectionTask.TASK_NAME;
                break;
            }
        }
        return taskName;
    }



    private void checkScheduledTask(String collectionName, ScheduledTaskType type, int repeatIntervalSec, boolean isRunning) throws InterruptedException {

        TaskName taskName = getTaskName(type);

        List<TaskName> taskNames = new ArrayList<TaskName>();
        taskNames.add(taskName);
        List<TaskDataViewWrapper> tasksBefore = taskService.getTaskWrappersExactCollection(collectionName, taskNames);
        // sleep for 2 interval, during this time task must be fired 2 times.
        Thread.sleep(repeatIntervalSec*2000);
        // get logs
        List<TaskDataViewWrapper> tasksAfter = taskService.getTaskWrappersExactCollection(collectionName, taskNames);
        List<TaskDataViewWrapper> targetTasks = (List<TaskDataViewWrapper>) CollectionUtils.disjunction(tasksBefore, tasksAfter);
        logger.info(MessageFormat.format("Found {0} tasks during {1} seconds interval", targetTasks.size(), repeatIntervalSec*2));
        for(TaskDataViewWrapper task: targetTasks){
            logger.info("Task uid: " + task.getTaskUid());
        }
        // Должен запуститься минимум один таск. Второй может не успеть запуститься в связи с тем,
        // что еще не завершился первый (повторный таск такого же типа по коллекции не запускается пока есть другой в статусе RUNNING)
        // Если не успевает запуститься даже один таск, надо увеличивать интервал в тесте
        // Максимум может запуститься 3 таска за 2 периода
        logger.info(MessageFormat.format("Expected found {0} tasks", isRunning ? "1-3" : "0"));
        if(isRunning){
            if(!(targetTasks.size() >= 1 || targetTasks.size() <= 3)){
                logger.error("Unexpected number of tasks");
            }
            Assert.assertTrue(targetTasks.size() >= 1 || targetTasks.size() <= 3);
        }else{
            if(!(targetTasks.size() == 0)){
                logger.error("Unexpected number of tasks");
            }
            Assert.assertTrue(targetTasks.size() == 0);
        }
    }

    public void scheduleTasksRunning(String collectionName, ScheduledTaskType type, int repeatIntervalSec) throws InterruptedException {
        checkScheduledTask(collectionName, type, repeatIntervalSec, true);
    }

    public void scheduleTasksNotRunning(String collectionName, ScheduledTaskType type, int repeatIntervalSec) throws InterruptedException {
        checkScheduledTask(collectionName, type, repeatIntervalSec, false);
    }

    private void changeScheduleActivityRequest(String collectionName, ScheduledTaskType type, boolean mustSetEnabled){

        logger.info(MessageFormat.format("{0} schedule {1} for collection {2}", mustSetEnabled ? "Enable" : "Disable", type, collectionName));
        Status status = serviceUtils.REST_TEMPLATE.postForObject(serviceUtils.serverURI + (mustSetEnabled ? CoordinatorConfigurationRestURIConstants.ENABLE_SCHEDULING : CoordinatorConfigurationRestURIConstants.DISABLE_SCHEDULING), null, Status.class, collectionName, type.toString());
        Assert.assertEquals(0, status.getCode());

    }

    private Boolean getScheduleEnabled(String collectionName, ScheduledTaskType type) throws InterruptedException {
        Boolean isEnabled = null;
        switch (type){
            case BUILD_SNIPPET:
            {
                isEnabled = testExecutor.getSnippetConfigurationWrapper(collectionName).getCron().isEnabled();
                break;
            }
            case INDEXING_DELTA:
            {
                CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(collectionName);
                isEnabled = collectionWrapper.getCronSchedule().isEnabled();
                break;
            }
            case MERGE_DELTA:
            {
                CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(collectionName);
                isEnabled = collectionWrapper.getMainCronSchedule().isEnabled();
                break;
            }
        }

        return isEnabled;
    }

    private void changeScheduleActivity(String collectionName, ScheduledTaskType type, boolean mustSetEnabled) throws InterruptedException {
        String targetScheduleStateString = mustSetEnabled ? "enable" : "disable";
        logger.info(MessageFormat.format("Try to {0} schedule {1} for collection {2}", targetScheduleStateString, type, collectionName));
        int delay = 1000;
        int waitTime = 0;
        int timeOut = 60000;

        boolean isEnabled = getScheduleEnabled(collectionName, type);

        //wait for required schedule status.
        // we cant switch off schedule if it is already switched off, because it could be switched off by some task,
        // and it will be switched on when task will be finished
        while (isEnabled == mustSetEnabled && waitTime < timeOut) {
            logger.info(MessageFormat.format("Schedule is not in required state. Must be {0} before operation", mustSetEnabled ? "disabled" : "enabled"));
            logger.info(MessageFormat.format("Wait for {0} seconds", delay/1000));
            Thread.sleep(delay);
            waitTime = waitTime + delay;
            isEnabled = getScheduleEnabled(collectionName, type);
        }
        if (isEnabled != mustSetEnabled) {
            changeScheduleActivityRequest(collectionName, type, mustSetEnabled);
        } else {
            logger.error(MessageFormat.format("Can't {0} schedule", targetScheduleStateString));
            Assert.assertTrue(false);
        }
    }

    public void disableSchedule(String collectionName, ScheduledTaskType type, int repeatIntervalSec) throws InterruptedException {

        disableScheduleWithoutCheckFiringTasks(collectionName, type);

        scheduleTasksNotRunning(collectionName, type, repeatIntervalSec);

    }

    public void disableScheduleWithoutCheckFiringTasks(String collectionName, ScheduledTaskType type) throws InterruptedException {

        changeScheduleActivity(collectionName, type, false);

        CronScheduleWrapper realCronScheduleWrapper = null;
        if(type == ScheduledTaskType.BUILD_SNIPPET){
            SnippetConfigurationWrapper snippetConfigurationWrapper = testExecutor.getSnippetConfigurationWrapper(collectionName);
            realCronScheduleWrapper = snippetConfigurationWrapper.getCron();
        }
        else{
            CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(collectionName);
            realCronScheduleWrapper = type == ScheduledTaskType.MERGE_DELTA ? collectionWrapper.getMainCronSchedule() : collectionWrapper.getCronSchedule();
        }
        Assert.assertFalse(realCronScheduleWrapper.isEnabled());

    }

    public void enableScheduleWithoutCheckFiringTasks(String collectionName, ScheduledTaskType type) throws InterruptedException {

        changeScheduleActivity(collectionName, type, true);

        CronScheduleWrapper realCronScheduleWrapper = null;
        if(type == ScheduledTaskType.BUILD_SNIPPET){
            SnippetConfigurationWrapper snippetConfigurationWrapper = testExecutor.getSnippetConfigurationWrapper(collectionName);
            realCronScheduleWrapper = snippetConfigurationWrapper.getCron();
        }
        else{
            CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(collectionName);
            realCronScheduleWrapper = type == ScheduledTaskType.MERGE_DELTA ? collectionWrapper.getMainCronSchedule() : collectionWrapper.getCronSchedule();
        }
        Assert.assertTrue(realCronScheduleWrapper.isEnabled());

    }

    public void enableSchedule(String collectionName, ScheduledTaskType type, int repeatIntervalSec) throws InterruptedException {

        changeScheduleActivity(collectionName, type, true);

        CronScheduleWrapper realCronScheduleWrapper = null;
        if(type == ScheduledTaskType.BUILD_SNIPPET){
            SnippetConfigurationWrapper snippetConfigurationWrapper = testExecutor.getSnippetConfigurationWrapper(collectionName);
            realCronScheduleWrapper = snippetConfigurationWrapper.getCron();
        }
        else{
            CollectionWrapper collectionWrapper = testExecutor.getCollectionWrapper(collectionName);
            realCronScheduleWrapper = type == ScheduledTaskType.MERGE_DELTA ? collectionWrapper.getMainCronSchedule() : collectionWrapper.getCronSchedule();
        }
        Assert.assertTrue(realCronScheduleWrapper.isEnabled());

        scheduleTasksRunning(collectionName, type, repeatIntervalSec);
    }



}
