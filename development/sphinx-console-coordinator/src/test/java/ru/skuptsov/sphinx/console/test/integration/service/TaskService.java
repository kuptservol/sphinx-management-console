package ru.skuptsov.sphinx.console.test.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.coordinator.controller.CoordinatorViewRestURIConstants;
import ru.skuptsov.sphinx.console.coordinator.model.*;
import ru.skuptsov.sphinx.console.coordinator.model.params.ActivityLogSearchParameters;
import ru.skuptsov.sphinx.console.coordinator.task.TaskName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lnovikova on 18.08.2015.
 */
@Service
public class TaskService {

    @Autowired
    ServiceUtils serviceUtils;

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public int getMaxTaskDuration(String collectionName, TaskName taskName){
        int result = 0;
        int duration;
        List<TaskName> taskNames = new ArrayList<TaskName>();
        taskNames.add(taskName);
        List<TaskDataViewWrapper> tasks = getTaskWrappers(collectionName, taskNames, TaskStatus.SUCCESS);
        for(TaskDataViewWrapper task : tasks){
            duration = (int)(task.getEndTime().getTime() - task.getStartTime().getTime());
            result = result > duration ? result : duration;
        }

        return result/1000;
    }

    public List<TaskDataViewWrapper> getTaskWrappers(String collectionName, List<TaskName> taskNames, TaskStatus taskStatus) {
        List<TaskDataViewWrapper> tasks = getTasks(collectionName, taskNames, taskStatus);

        List<TaskDataViewWrapper> taskWrappers = new ArrayList<TaskDataViewWrapper>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object map : tasks) {
            TaskDataViewWrapper taskDataViewWrapper = objectMapper.convertValue(map, TaskDataViewWrapper.class);
            taskWrappers.add(taskDataViewWrapper);
        }

        return taskWrappers;
    }

    public List<TaskDataViewWrapper> getTaskWrappers(String collectionName, List<TaskName> taskNames) {
        return getTaskWrappers(collectionName, taskNames, null);
    }

    public List<TaskDataViewWrapper> getTaskWrappersExactCollection(final String collectionName, List<TaskName> taskNames) {
        List<TaskDataViewWrapper> tasks = getTaskWrappers(collectionName, taskNames, null);

        /*делаем дополнительную фильтрацию по коллекции на точное совпадение имени, т.к. таски ищутся по вхождению*/
        CollectionUtils.filter(tasks, new Predicate() {
            public boolean evaluate(Object object) {
                return ((TaskDataViewWrapper)object).getCollectionName().equals(collectionName);
            }
        });

        return tasks;
    }

    public List<TaskDataViewWrapper> getTasks(String collectionName) {
        return getTasks(collectionName, null, null);
    }

    private List<TaskDataViewWrapper> getTasks(String collectionName, List<TaskName> taskNames) {
        return getTasks(collectionName, taskNames, null);
    }

    private List<TaskDataViewWrapper> getTasks(String collectionName, List<TaskName> taskNames, TaskStatus taskStatus) {
        ActivityLogSearchParameters activityLogSearchParameters = new ActivityLogSearchParameters();
        activityLogSearchParameters.setCollectionName(collectionName);
        activityLogSearchParameters.setPageSize(1);
        activityLogSearchParameters.setTaskNames(taskNames);
        activityLogSearchParameters.setTaskStatus(taskStatus);
        ResponseEntity<ListDataViewWrapper> tasksResponse = serviceUtils.REST_TEMPLATE.postForEntity(serviceUtils.serverURI + CoordinatorViewRestURIConstants.TASKS,
                activityLogSearchParameters, ListDataViewWrapper.class);


        ListDataViewWrapper<List<TaskDataViewWrapper>> tasks = (ListDataViewWrapper<List<TaskDataViewWrapper>>) tasksResponse.getBody();

        if (tasks != null && tasks.getList() != null) {
            logger.info("RETRIVED TASKS LIST, SIZE: " + tasks.getList().size());
            return tasks.getList();
        }


        return null;
    }
}
