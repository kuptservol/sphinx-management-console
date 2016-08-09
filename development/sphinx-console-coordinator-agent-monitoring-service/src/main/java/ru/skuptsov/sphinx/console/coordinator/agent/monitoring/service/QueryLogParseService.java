package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service;

import java.text.ParseException;
import java.util.Date;

public interface QueryLogParseService {
    String getQueryFromLine(String line);
    Date parseDate(String line) throws ParseException;
    Integer parseTotalTime(String line);
    Integer parseResultCount(String line);
    Integer parseOffSet(String line);
}
