package ru.skuptsov.sphinx.console.coordinator.model.common;

/**
 * Created by lnovikova on 30.09.2015.
 */
public enum DateDetailing {
    DATE("День"),
    HOUR("Час"),
    HALF_AN_HOUR("Полчаса"),
    TEN_MINUTES("10 минут"),
    MINUTE("Минута"),
    MILLISECOND("Миллисекунда");

    private String title;

    DateDetailing(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
