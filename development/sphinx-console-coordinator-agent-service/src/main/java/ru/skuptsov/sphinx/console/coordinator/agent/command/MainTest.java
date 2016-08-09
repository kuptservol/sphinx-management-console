package ru.skuptsov.sphinx.console.coordinator.agent.command;

import ru.skuptsov.sphinx.console.coordinator.model.Status;
import ru.skuptsov.sphinx.console.coordinator.model.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 07.08.14
 * Time: 23:56
 * To change this template use File | Settings | File Templates.
 */
public class MainTest {
    private static Map<String, Process> runningProcesses = new HashMap<String, Process>();

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            String line;
//            Process process = Runtime.getRuntime().exec("C:\\Program Files\\Internet Explorer\\iexplore.exe");
            Process process = Runtime.getRuntime().exec("iexplore.exe", null, new File("C://Program Files/Internet Explorer"));
            runningProcesses.put("1", process);
            destroy("1");
            int i = 0;

            createDir();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void destroy(String uid) {
        runningProcesses.get(uid).destroy();
    }

    public static void createDir() {
        File dir = new File("name2313213");
        if(!dir.exists()) {
            dir.mkdirs();
        }
    }
}
