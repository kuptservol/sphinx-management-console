package ru.skuptsov.sphinx.console.admin.service.impl;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.skuptsov.sphinx.console.admin.service.api.FileService;

@Service
public class FileServiceImpl implements FileService {

    protected static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public Reader getReader(String prefixChangesetPath, String path) throws FileNotFoundException, UnsupportedEncodingException {
        Reader reader;

        try {
            FileInputStream fstream1 = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream1);
            reader = new InputStreamReader(in,"UTF-8");
        } catch (FileNotFoundException exc) {
//            path = System.getProperty("user.dir") + "\\" + path;
            //String tmpPath = FileServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = prefixChangesetPath + path;
            reader = new FileReader(path);
        }

        return reader;
    }

    @Override
    public String getFileName(String path) {
        String fileName;
        File file;
        try {
            file = new File(path);
            fileName = file.getName();
        } catch (Throwable exc) {
            path = System.getProperty("user.dir") + "\\" + path;
            file = new File(path);
            fileName = file.getName();
        }

        return fileName;
    }

    @Override
    public String getFileDirPath(String path) {
        String fileDirPath = "";
        if(path != null) {
            path = path.replace("\\", "/");
            fileDirPath = path.substring(0, path.lastIndexOf("/") + 1);
        }

        return fileDirPath;
    }

    @Override
    public String readFile(String prefixChangesetPath, String path) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(getReader(prefixChangesetPath, path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }

            return sb.toString();
        } catch(Throwable exc) {
            logger.error("Error during reading file", exc);
            return null;
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (Throwable exc) {
                logger.error("Error during closing reader",exc);
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