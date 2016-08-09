package ru.skuptsov.sphinx.console.test.integration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

/**
 * Created by lnovikova on 16.07.2015.
 */
@Service
public class CommandLineService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int MAX_OUTPUT_READING_LENGTH = 1000;
    private static final String SSH_COMMAND_TEMPLATE = "{0} {1}@{2} {3}";

    public String executeSshCommandWithResult(String sshClient, String ip, String userName, String command) throws IOException, InterruptedException {
        String sshCommand = getSshCommandText(sshClient, ip, userName, command);
        return executeCommandWithResult(sshCommand);
    }

    public String getSshCommandText(String sshClient, String ip, String userName, String command){
        return MessageFormat.format(SSH_COMMAND_TEMPLATE, sshClient, userName, ip, command);
    }

    public String executeCommandWithResult(String command) throws IOException, InterruptedException {
        logger.info("Execute command: " + command);
//        Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
        Process process = Runtime.getRuntime().exec(command);

        StringBuffer output = new StringBuffer();

        try {

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null && output.length() < MAX_OUTPUT_READING_LENGTH) {
                output.append(line + "\n");
            }

            logger.info("Execution result of command: <<" + command + ">>: " + output.toString());

            process.waitFor();
            logger.info("exit value: " + process.exitValue());

        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return output.toString();

    }

}
