package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;

/**
 * Created by Developer on 12.03.2015.
 */
public enum TaskRunningStatus implements Serializable {
    RUNNING,
    COMPLETE,
    FAIL,
    EMPTY
}
