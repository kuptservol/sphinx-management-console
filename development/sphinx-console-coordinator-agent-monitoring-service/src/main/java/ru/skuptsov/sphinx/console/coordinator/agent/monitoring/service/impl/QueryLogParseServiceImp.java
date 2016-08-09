package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.QueryLogParseService;
import ru.skuptsov.sphinx.console.coordinator.exception.SearchStringNotFoundException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class QueryLogParseServiceImp implements QueryLogParseService{


    protected static final Logger logger = LoggerFactory.getLogger(QueryLogParseServiceImp.class);

    @Override
    public String getQueryFromLine(String line){
        String query = line.substring(line.indexOf("*/") + 2, line.lastIndexOf("/*") - 1);
        return query;
    }

    @Override
    public Date parseDate(String line) throws ParseException {
        return parseLogDate(line.substring(7, 31));
    }

    @Override
    public Integer parseTotalTime(String line){
        String searchString = "real";
        int searchStringLength = searchString.length();
        int searchStringStartIndex = line.indexOf(searchString);
        if(searchStringStartIndex == -1) {throw new SearchStringNotFoundException();}
        int startIndex = searchStringStartIndex + searchStringLength + 1;
        String resultString = line.substring(startIndex , startIndex + 5);
        BigDecimal number = new BigDecimal(resultString).multiply(new BigDecimal(1000));
        return (number.intValue());
    }

    @Override
    public Integer parseResultCount(String line){
        String searchString = "found";
        int searchStringLength = searchString.length();
        int searchStringStartIndex = line.indexOf(searchString);
        if(searchStringStartIndex == -1) {throw new SearchStringNotFoundException();}
        int startIndex = searchStringStartIndex + searchStringLength + 1;
        String resultString = line.substring(startIndex, line.indexOf("*/") - 1);
        return (Integer.parseInt(resultString));
    }

    @Override
    public Integer parseOffSet(String line) {
        Pattern offsetPattern = Pattern.compile(".*limit\\s+(\\d+)\\s*,\\s*\\d.*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = offsetPattern.matcher(line);
        if (matcher.matches()) {
            return (Integer.parseInt(matcher.group(1)));
        } else {
            return 0;
        }
    }

    private Date parseLogDate(String dateString) throws ParseException {
        DateFormat format = new SimpleDateFormat("MMM dd HH:mm:ss.SSS yyyy", Locale.ENGLISH);
        Date date = format.parse(dateString);
        return date;
    }

}
