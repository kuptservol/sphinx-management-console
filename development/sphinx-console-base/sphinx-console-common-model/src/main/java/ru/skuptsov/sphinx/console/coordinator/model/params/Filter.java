package ru.skuptsov.sphinx.console.coordinator.model.params;

import java.io.Serializable;

/**
 * Created by aefimov on 09.09.14.
 */
public class Filter implements Serializable {
    private String property;
    private String anyMatch;
    private String value;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getAnyMatch() {
        return anyMatch;
    }

    public void setAnyMatch(String anyMatch) {
        this.anyMatch = anyMatch;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

