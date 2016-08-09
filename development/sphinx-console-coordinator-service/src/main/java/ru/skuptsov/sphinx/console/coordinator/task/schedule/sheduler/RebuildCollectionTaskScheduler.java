package ru.skuptsov.sphinx.console.coordinator.task.schedule.sheduler;

import org.springframework.stereotype.Component;

import ru.skuptsov.sphinx.console.coordinator.model.ScheduledTaskType;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.service.AbstractScheduleService;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.service.RebuildCollectionScheduleService;
import ru.skuptsov.sphinx.console.util.spring.ApplicationContextProvider;

@Component
public class RebuildCollectionTaskScheduler extends AbstractTaskScheduler{

    @Override
    public String getGroupName(){
        return "INDEXER_DELTA";
    }

    @Override
    protected AbstractScheduleService getScheduleService() {
        return (RebuildCollectionScheduleService)ApplicationContextProvider.getBean("rebuildCollectionScheduleService");
    }

    @Override
    protected ScheduledTaskType getScheduledTaskType() {
        return ScheduledTaskType.INDEXING_DELTA;
    }

}