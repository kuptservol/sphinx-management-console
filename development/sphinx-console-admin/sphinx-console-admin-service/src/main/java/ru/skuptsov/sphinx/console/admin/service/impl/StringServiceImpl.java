package ru.skuptsov.sphinx.console.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.admin.service.api.FileService;
import ru.skuptsov.sphinx.console.admin.service.api.StringService;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Developer on 05.06.2015.
 */
@Service
public class StringServiceImpl implements StringService {
    @Autowired
    private FileService file;

    public String getFormattedString(String prefixChangesetPath, String filePath, String propsFilePath) {
        String formattedString = file.readFile(prefixChangesetPath, filePath);

        if(propsFilePath != null && !"".equals(propsFilePath)) {
            Properties props = new Properties();
            try {
                props.load(file.getReader(prefixChangesetPath, propsFilePath));
            } catch (IOException e) {
                throw new RuntimeException("RestServiceImpl can't read json properties file.", e);
            }
            formattedString = formatString(formattedString, props);
        }

        return formattedString;
    }

//    public String getFormattedString(String filePath, String propsFilePath) {
//        return getFormattedString(filePath, propsFilePath);
//    }

    private String formatString(String template, Properties properties) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(template);
        // StringBuilder cannot be used here because Matcher expects StringBuffer
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            if (properties.containsKey(matcher.group(1))) {
                String replacement = (String)properties.get(matcher.group(1));
                // quote to work properly with $ and {,} signs
                matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
            }
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }
}
