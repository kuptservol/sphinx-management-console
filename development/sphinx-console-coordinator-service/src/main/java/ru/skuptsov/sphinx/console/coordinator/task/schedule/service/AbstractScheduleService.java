package ru.skuptsov.sphinx.console.coordinator.task.schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.skuptsov.sphinx.console.coordinator.jmx.JmxService;

/**
 * Created by lnovikova on 18.08.2015.
 */
public abstract class AbstractScheduleService {

    @Autowired
    JmxService jmxService;

    private String collectionName;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

}
