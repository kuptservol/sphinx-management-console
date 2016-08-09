package ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler;

import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.service.AbstractScheduleService;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.service.MergeCollectionScheduleService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

@Component
public class MergeCollectionTaskScheduler extends AbstractTaskScheduler{

    @Override
    public String getGroupName(){
        return "INDEXER_MERGE";
    }

    @Override
    protected AbstractScheduleService getScheduleService() {
        return (MergeCollectionScheduleService)ApplicationContextProvider.getBean("mergeCollectionScheduleService");
    }

    @Override
    protected ScheduledTaskType getScheduledTaskType() {
        return ScheduledTaskType.MERGE_DELTA;
    }

}
