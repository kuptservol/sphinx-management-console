package ru.skuptsov.sphinx.console.coordinator.model;

/**
 * Created by lnovikova on 02.10.2015.
 */
public class ValueTitle {

    private String value;
    private String title;

    public ValueTitle(String value, String title) {
        this.value = value;
        this.title = title;
    }

    public ValueTitle() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
