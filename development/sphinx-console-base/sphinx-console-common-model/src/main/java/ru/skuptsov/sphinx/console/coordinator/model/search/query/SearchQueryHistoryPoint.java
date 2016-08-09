package ru.skuptsov.sphinx.console.coordinator.model.search.query;

import java.math.BigDecimal;

public class SearchQueryHistoryPoint {

    private long timestamp;
    private BigDecimal[] values;

    public long getDate() {
        return timestamp;
    }

    public SearchQueryHistoryPoint() {
    }

    public SearchQueryHistoryPoint(long timestamp, BigDecimal... values) {
        this.timestamp = timestamp;
        this.values = values;
    }

    public void setDate(long timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal[] getValues() {
        return values;
    }

    public void setValues(BigDecimal[] values) {
        this.values = values;
    }
}
