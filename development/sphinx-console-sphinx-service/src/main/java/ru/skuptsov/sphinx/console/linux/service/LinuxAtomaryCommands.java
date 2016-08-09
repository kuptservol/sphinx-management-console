package ru.skuptsov.sphinx.console.linux.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;


@Component("LinuxAtomaryCommands")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class LinuxAtomaryCommands {
    private static final Logger logger = LoggerFactory.getLogger(LinuxAtomaryCommands.class);

    private final String SCP_TEMPLATE = "{0}@{1}:{2}{3}{4}";
    
    private final String RSYNC_TEMPLATE = "{0}@{1}:{2}{3}"; //root@10.249.140.239:/root/ 

    public static final String STARTUP_COMMAND = " chkconfig";

    @Value("${base.dir}")
    private String baseDirPath;
    
    @Value("${service.dir}")
    private String serviceDir;

    @Value("${os.user}")
    private String user;

    @Value("${sudo.path}")
    private String sudoPath;

    @Value("${service.path}")
    private String servicePath;

    private String serviceNamePrefix = "sphinx.console-service_";

    /*Вывод выполнения команды может быть очень большим и достигать сотен мегабайт, поэтому обрезаем до 1000 символов*/
    private static final int MAX_OUTPUT_READING_LENGTH = 1000;

    @PostConstruct
    private void processBaseDir() throws IOException {
    	logger.info("BASE DIR: " + baseDirPath);
        File baseDir = new File(baseDirPath);
        if(!baseDir.exists()) {
            baseDir.mkdirs();
        }
        //chownDeep(baseDirPath);
    }

    private String getErrorStringStart(InputStream errorStream) throws IOException {
        logger.debug("EXECUTE getErrorStringStart");
        StringBuilder errorOutput = new StringBuilder();

        BufferedReader errReader = new BufferedReader(new InputStreamReader(errorStream));

        String errLine = "";
        while ((errLine = errReader.readLine())!= null && errorOutput.length() < MAX_OUTPUT_READING_LENGTH) {
            errorOutput.append(errLine + "\n");
        }

        return errorOutput.toString();
    }

    private String getErrorStringTail(InputStream errorStream) throws IOException {
        logger.debug("EXECUTE getErrorStringTail");
        byte[] buf = new byte[1000];
        byte[] prevBuf = new byte[1000];
        while (errorStream.read(buf) != -1) {
//            logger.info("EXECUTION RESULT OF getErrorStringTail - " + new String(buf) + ".");
            if(errorStream.available() > 0) {
                prevBuf = buf;
                buf = new byte[1000];
            }
        }
        errorStream.close();

        String result = (new String(prevBuf) + new String(buf)).trim();

        return result.length() > MAX_OUTPUT_READING_LENGTH ? result.substring(result.length() - MAX_OUTPUT_READING_LENGTH, result.length()) : result;
    }

    private String getErrorString(InputStream errorStream, boolean getErrorTail) throws IOException {
        String errorString;
        if(getErrorTail) {
            errorString = getErrorStringTail(errorStream);
        } else {
            errorString = getErrorStringStart(errorStream);
        }

        return errorString;
    }

    public void executeProcess(String processName) throws IOException {
        String command = MessageFormat.format("{0} {1} {2} start", sudoPath, servicePath, getServiceName(processName));
        Process process = Runtime.getRuntime().exec(command);

        String errorText = "";
        StringBuffer output = new StringBuffer();
        try {
			
			BufferedReader reader = 
			         new BufferedReader(new InputStreamReader(process.getInputStream()));
			 
			    String line = "";			
			    while ((line = reader.readLine())!= null) {
                    // Необходимо читать вывод процесса до победного конца
                    // см. http://stackoverflow.com/questions/5483830/process-waitfor-never-returns

                    // но это значит, что нам интересно всё, о чем сообщает searchd, поэтому:
                    if (output.length() < MAX_OUTPUT_READING_LENGTH) {
                        output.append(line + "\n");
                    }
			    }
			    
			    errorText = getErrorStringStart(process.getErrorStream());

		        logger.debug("EXECUTION RESULT OF PROCESS: <<" + command + ">>" + ": ");
		        logger.debug("RESULT: " + output.toString());
			
			    process.waitFor();
                logger.debug("exit: " + process.exitValue());

            if (process.exitValue() == 0) {
	            	logger.info("EXECUTION RESULT OF PROCESS: <<" + command + ">> : SUCCESS");
	            } else {
                    logger.info("EXECUTION RESULT OF PROCESS: <<" + command +  ">> : FAILURE!");
                    String error = (StringUtils.isNotEmpty(output.toString()) ? output.toString() : errorText);
                    if (StringUtils.isEmpty(error)) {
                    	error = "EXECUTION RESULT OF PROCESS: <<" + command + ">> : FAILURE!";
                    }
                    throw new ApplicationException("error occured while execution operation on sphinx: " + error);
	            }
	              
		} catch (InterruptedException e) {
            logger.error("Error during executeProcess", e);
			throw new ApplicationException(e);
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
    }
    
    public String executeCommandWithResult(String command) throws IOException {
    	Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});

        StringBuffer output = new StringBuffer();

        try {
			
			BufferedReader reader = 
			         new BufferedReader(new InputStreamReader(process.getInputStream()));
			 
                String line = "";
			    while ((line = reader.readLine())!= null) {
                    // см. комментарий в методе executeProcess
                    if (output.length() < MAX_OUTPUT_READING_LENGTH) {
                        output.append(line + "\n");
                    }
			    }
			    
			    logger.debug("EXECUTION RESULT OF COMMAND: <<" + command + ">>: ");
			    logger.debug("RESULT: " + output.toString());
			    
			    process.waitFor();
	            logger.debug("exit: " + process.exitValue());

                logger.info("EXECUTION RESULT OF COMMAND: <<" + command + ">>: " + (process.exitValue() == 0 ? "SUCCESS" : "FAILURE"));

        } catch (Throwable e) {
            logger.error("Error during executeCommandWithResult", e);
			throw new ApplicationException(e);
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
        
        return output.toString();

    }

    public void executeCommand(String command) throws IOException {
        executeCommand(command, false);
    }

    public void executeCommand(String command, boolean getErrorTail) throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
        String errorText = "";
        StringBuilder output = new StringBuilder();
        try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			 
            String line = "";
            while ((line = reader.readLine())!= null) {
                // см. комментарий в методе executeProcess
                if (output.length() < MAX_OUTPUT_READING_LENGTH) {
                    output.append(line + "\n");
                }
            }

            logger.debug("EXECUTION RESULT OF COMMAND: <<" + command + ">>: ");
            logger.debug("RESULT: " + output.toString());

            logger.debug("EXECUTION getErrorString: getErrorTail - " + getErrorTail + ", process.getErrorStream() - " + process.getErrorStream() + ".");
            errorText = getErrorString(process.getErrorStream(), getErrorTail);
            logger.debug("EXECUTION RESULT OF errorText - " + errorText + ".");

//            logger.info("LinuxAtomaryCommands process.waitFor().");
            process.waitFor();
            logger.debug("exit: " + process.exitValue());
            if (process.exitValue() == 0 && !output.toString().contains("ERROR")) {
                logger.debug("SUCCESS!");
                logger.info("EXECUTION RESULT OF COMMAND: <<" + command + ">>: SUCCESS");
            } else {
                logger.debug("FAILURE!");
                logger.info("EXECUTION RESULT OF COMMAND: <<" + command + ">>: FAILURE");
                throw new ApplicationException("error occurred while execution operation on sphinx: " + (StringUtils.isNotEmpty(output.toString()) ? output.toString() : errorText));
            }
		} catch (InterruptedException e) {
            logger.info("LinuxAtomaryCommands InterruptedException: <<" + e.getMessage() + ">>.");
            throw new ApplicationException("error occurred while execution command: " + command + "\n output:" + (StringUtils.isNotEmpty(output.toString()) ? output.toString() : errorText));
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
    }

    public void destroyProcess(String processName, boolean ignoreFailure) throws IOException {
        String command = MessageFormat.format("{0} {1} {2} stop", sudoPath, servicePath, getServiceName(processName));
        Process process = Runtime.getRuntime().exec(command);

        StringBuffer output = new StringBuffer();

        try {

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                // см. комментарий в методе executeProcess
                if (output.length() < MAX_OUTPUT_READING_LENGTH) {
                    output.append(line + "\n");
                }
            }

            logger.info("EXECUTING OF PROCESS: << " + command);

            process.waitFor();
            logger.debug("exit: " + process.exitValue());

            logger.info("EXECUTION RESULT OF PROCESS: <<" + command + ">> : " + (process.exitValue() == 0 ? "SUCCESS" : "FAILURE"));

            if (process.exitValue() != 0) {
                logger.error("EXECUTION RESULT OF PROCESS: <<" + command + ">> : FAILURE");
                if (ignoreFailure) {
                    logger.error("Failure while stopping sphinx service. Ignore failure due to possible inconsistent state of service.");
                } else {
                    throw new ApplicationException("error occurred while execution operation on sphinx: " + output.toString());
                }
            }
        } catch (InterruptedException e) {
            logger.error("Error during destroyProcess", e);
            throw new ApplicationException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public void createDir(String relativeDirName) throws IOException {
        logger.info("Creating directory directory: " + baseDirPath + relativeDirName);
        executeCommand("mkdir -p " + baseDirPath + relativeDirName);
        //chownDeep(baseDirPath + relativeDirName);
    }

    public void createDirs(String relativeDirNames) throws IOException {
        for(String name : relativeDirNames.split(", ")) {
            createDir(name);
        }
    }

    public void deleteDirOrFile(String relativeDirName) throws IOException {
        executeCommand("rm -rf " + baseDirPath + relativeDirName);
    }

    public void deleteAllFilesInDir(String relativeDirName) throws IOException {
        logger.info("Delete all files in directory: " + baseDirPath + relativeDirName);
        executeCommand("rm -rf " + baseDirPath + relativeDirName + "/*");
    }

    public void deleteDirsOrFiles(String relativeDirNames) throws IOException {
        for(String name : relativeDirNames.split(", ")) {
            deleteDirOrFile(name);
        }
    }

    public void createServiceFile(String processName, String sphinxServiceContent) throws IOException {
        String serviceFileName = serviceDir + getServiceName(processName);
//        File file = new File(serviceFileName);
//        if(!file.exists()) {
//            if(!file.createNewFile()) {
//                throw new ApplicationException("Can't create file " + serviceFileName + "!!!");
//            } else {
//                logger.info("Create file " + serviceFileName + " success!!!");
//            }
//        }
//        BufferedWriter out = new BufferedWriter(new FileWriter(file));
//        out.write(sphinxServiceContent);
//        out.close();
        String createServiceFileCommand = "echo '" + sphinxServiceContent + "' | " + sudoPath + " tee --append " + serviceFileName + " > /dev/null ";
        executeCommand(createServiceFileCommand);
        executeCommand(sudoPath + " chmod a+x " + serviceFileName);
    }

    public void deleteProcessFile(String processName) throws IOException {
        executeCommand(sudoPath + " rm -rf " + serviceDir + getServiceName(processName));
    }

    public void renameFiles(String dir, String... relativeFileNames) throws IOException {
        for(String relativeFileName : relativeFileNames) {
            String[] fromTo = relativeFileName.split(" ");
            renameFile(dir + fromTo[0], dir + fromTo[1]);
        }
    }

    public void copyFiles(String fromDir, String toDir, String... relativeFileNames) throws IOException {
        for(String relativeFileName : relativeFileNames) {
            copyFile(fromDir + relativeFileName, toDir + relativeFileName);
        }
    }

    public void copyAndRenameFiles(String fromDir, String toDir, String... relativeFileNames) throws IOException {
        for(String relativeFileName : relativeFileNames) {
            String[] fromTo = relativeFileName.split(" ");
            copyFile(fromDir + fromTo[0], toDir + fromTo[1]);
        }
    }

    public void scpAndRenameFiles(String fromDir, String toDir, String... relativeFileNames) throws IOException {
        for(String relativeFileName : relativeFileNames) {
            scp(fromDir + relativeFileName, toDir);
        }
    }

    public void scpCopyIndexFiles(String fromIndexFileName, String fromDir, String toDir) throws IOException {
       // String from = baseDirPath + fromDir + fromIndexFileName;
        String from = fromIndexFileName;
        String to = baseDirPath + toDir;
        logger.info("SCP COPY FILE: from " + from + " to " + to);
        scp(from, to);
    }

    public void scpCopyIndexFiles(String fromIndexFileName, String fromDir, String toDir, String fromServer, String toServer) throws IOException {
        //String from = MessageFormat.format(SCP_TEMPLATE, user, fromServer, baseDirPath, fromDir, fromIndexFileName);
    	//String from = baseDirPath + fromDir + fromIndexFileName;
    	String from = fromIndexFileName;
    	String to = MessageFormat.format(SCP_TEMPLATE, user, toServer, baseDirPath, toDir, "");
        logger.info("SCP COPY FILE: from " + from + " to " + to);
        scp(from, to);
    }

    public void renameFile(String oldRelativeName, String newRelativeName) throws IOException {
        File file = new File(baseDirPath + oldRelativeName);
        if(file.exists()) {
            file.renameTo(new File(baseDirPath + newRelativeName));
            //chownDeep(newRelativeName);
        }
    }

    public void chown(String path) throws IOException {
        executeCommand("chown " + user + " " + path);
    }

    public void chownDeep(String path) throws IOException {
        executeCommand("chown -R " + user + " " + path);
    }

    public void createFileWithNewName(String oldRelativeName, String newRelativeName) throws IOException {
        logger.info("ABOUT TO CREATE COPY OF FILE, oldRelativeName: " + oldRelativeName + ", newRelativeName: " + newRelativeName);
    	File file = new File(oldRelativeName);
    	
        if(file.exists()) {
        	logger.info("EXECUTE COPING...");
        	int status = FileCopyUtils.copy(file, new File(newRelativeName));
            logger.info("STATUS Of COPING: " + status);

            logger.info("SETTING OWNER");
            //chownDeep(newRelativeName);
        }
    }

    public void replaceFile(String relativeFileName, String content) throws IOException {
        // Create temp file.
        File temp = File.createTempFile("TempFileName", ".tmp", new File(baseDirPath));
        // Write to temp file
        BufferedWriter out = new BufferedWriter(new PrintWriter(temp, "UTF-8"));
        out.write(content);
        out.close();
        // Original file
        File orig = new File(baseDirPath + relativeFileName);
        // Copy the contents from temp to original file
        FileChannel src = new FileInputStream(temp).getChannel();
        FileChannel dest = new FileOutputStream(orig).getChannel();
        dest.transferFrom(src, 0, src.size());
        temp.delete();
        //chownDeep(baseDirPath + relativeFileName);
    }

    public void addProcessToStartup(String processName) throws IOException {
        executeCommand(MessageFormat.format("{0} {1} {2} on", sudoPath, STARTUP_COMMAND, getServiceName(processName)));
    }

    public void removeProcessFromStartup(String processName) throws IOException {
        executeCommand(MessageFormat.format("{0} {1} {2} off", sudoPath, STARTUP_COMMAND, getServiceName(processName)));
    }

    public String getServiceName(String processName){
        return serviceNamePrefix + processName;
    }

    public void copyFile(String fromFileName, String toFileName) throws IOException {
        logger.info("COPY FILE: from " + fromFileName + " to " + toFileName);
        // Create temp file.
        File src = new File(baseDirPath + fromFileName);
        //TODO узнать на счет удаления
        // Original file
        File dest = new File(baseDirPath + toFileName);
        // Copy the contents from temp to original file
        FileChannel srcCh = new FileInputStream(src).getChannel();
        FileChannel destCh = new FileOutputStream(dest).getChannel();
        destCh.transferFrom(srcCh, 0, srcCh.size());
        //chownDeep(baseDirPath + toFileName);
    }

    public void scp(String from, String to) throws IOException {
        executeCommand("scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -r -C -c blowfish " + from + " " + to);
        //executeCommand("sudo -u " + user + " scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -r -C -c blowfish " + from + " " + to);
    }
    
    //http://rtfm.co.ua/linux-primery-ispolzovaniya-rsync/
    public void rsync(String fromServer, String toServer, String fromDir, String toDir) throws IOException {
    	String from = /*MessageFormat.format(RSYNC_TEMPLATE, user, fromServer, baseDirPath, fromDir)*/baseDirPath + fromDir;
    	String to = MessageFormat.format(RSYNC_TEMPLATE, user, toServer, baseDirPath, toDir);
    	logger.info("EXECUTE: " + "rsync -avzh --delete " + from + " " + to);
    	executeCommand(sudoPath + " -u "  + user + " rsync -avzh --delete " + from + " " + to);
    }
}
