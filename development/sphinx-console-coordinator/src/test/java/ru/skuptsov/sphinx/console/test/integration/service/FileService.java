package ru.skuptsov.sphinx.console.test.integration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileService {

    protected final static Logger logger = LoggerFactory.getLogger(FileService.class);

    public String readFile(String filePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }

            return sb.toString();
        } catch(Throwable exc) {
            logger.error(exc.getMessage(), exc);
            return null;
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (Throwable exc) {
                logger.error(exc.getMessage(), exc);
            }
        }
    }

    public boolean isFileValid(String path) {
        boolean isFileValid = false;
        File file;
        try {
            file = new File(path);
            isFileValid = file.canRead();
        } catch (Throwable exc) {
        }

        return isFileValid;
    }
}