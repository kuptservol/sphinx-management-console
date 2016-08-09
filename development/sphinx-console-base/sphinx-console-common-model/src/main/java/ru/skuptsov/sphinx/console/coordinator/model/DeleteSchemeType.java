package ru.skuptsov.sphinx.console.coordinator.model;

/**
 * Created by Andrey
 */
public enum DeleteSchemeType {
    BUSINESS_FIELD("business")/*, REQUEST("sql_query_killlist")*/;

    private String value;

    DeleteSchemeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
