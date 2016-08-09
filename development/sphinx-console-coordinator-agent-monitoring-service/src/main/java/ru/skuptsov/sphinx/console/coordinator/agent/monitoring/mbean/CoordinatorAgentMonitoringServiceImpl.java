package ru.skuptsov.sphinx.console.coordinator.agent.monitoring.mbean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.api.QueryLogService;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.SphinxQLConsoleService;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.SphinxQLService;
import ru.skuptsov.sphinx.console.coordinator.agent.monitoring.service.api.CoordinatorAgentMonitoringService;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.model.ProcessStatus;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQuery;
import ru.skuptsov.sphinx.console.coordinator.model.sphinxQL.SphinxQLMultiResult;
import ru.skuptsov.sphinx.console.sphinx.service.impl.SphinxIndexerServiceImpl;

import java.io.*;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;

@Component
@ManagedResource(objectName = CoordinatorAgentMonitoringServiceImpl.MBEAN_NAME,  description = "") 
public class CoordinatorAgentMonitoringServiceImpl implements CoordinatorAgentMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(CoordinatorAgentMonitoringServiceImpl.class);
    public static final String MBEAN_NAME = "coordinator.agent.mbeans:type=config,name=CoordinatorAgentMonitoringService";

    @Autowired
    SphinxQLService sphinxQLService;

    @Autowired
    SphinxQLConsoleService sphinxQLConsoleService;

    @Autowired
    QueryLogService queryLogService;

    @Value("${base.dir}")
    private String baseDir;
    
    @Value("${indexer.work.dir}")
    private String workDir;
    
    @Value("${indexer.processname}")
    private String indexerName;

    @Value("${sudo.path}")
    private String sudoPath;

    @Value("${service.path}")
    private String servicePath;

    /*Вывод выполнения команды может быть очень большим и достигать сотен мегабайт, поэтому обрезаем до 1000 символов*/
    private static final int MAX_OUTPUT_READING_LENGTH = 1000;

    @ManagedOperation(description = "")
	public String test() {
	    logger.debug("CoordinatorAgentMonitoringServiceImpl-" + System.currentTimeMillis());
	    
	    return "";
	}

    /**
     * processName - sphinx process name
     */
	@Override
    @ManagedOperation(description = "")
    public ProcessStatus isProcessAlive(String processName) {
        logger.debug("ABOUT TO RETRIEVE PROCESS STATUS: " + processName);
		ProcessStatus processStatus = ProcessStatus.FAILURE;
        try {
            Process process = Runtime.getRuntime().exec(sudoPath + " " + servicePath + " " + "sphinx.console-service_" + processName + " status");
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(process.getInputStream()));
            String line = null;
            String text = "";
            while ((line = input.readLine()) != null) {
                text += line;
            }
            input.close();

            logger.debug("PROCESS STATUS COMMAND ANSWER: " + text);
            processStatus = !text.equals("") ? ProcessStatus.SUCCESS : ProcessStatus.FAILURE;
            
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug("PROCESS STATUS: " + processStatus.toString());
        return processStatus;
	}

	@Override
	@ManagedOperation(description = "")
	public Boolean isCurrentlyIndexing(String processName) {

        return isProcessRunning(MessageFormat.format(SphinxIndexerServiceImpl.INDEXER_PATTERN, workDir, indexerName, baseDir, processName));

	}

    @Override
    @ManagedOperation(description = "")
    public Boolean isCurrentlyIndexing(String processName, String indexName) {

        return isProcessRunning(MessageFormat.format(SphinxIndexerServiceImpl.INDEXER_INDEX_PATTERN, workDir, indexerName, baseDir, processName, indexName));

    }

    @Override
    @ManagedOperation(description = "")
    public Boolean isCurrentlyMerging(String processName) {

        return isProcessRunning(MessageFormat.format(SphinxIndexerServiceImpl.INDEXER_MERGE_PATTERN, workDir, indexerName, baseDir, processName));

    }

    private Boolean isProcessRunning(String processString) {
        Process process = null;
        String command = "pgrep -f \"" + processString+"\"";
        try {
            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
        } catch (IOException e1) {
            logger.error(e1.getMessage(), e1);
            throw new ApplicationException(e1);
        }
        StringBuffer output = new StringBuffer();

        try {


            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null && output.length() < MAX_OUTPUT_READING_LENGTH) {
                output.append(line + "\n");
            }

            logger.debug(MessageFormat.format("EXECUTION RESULT OF COMMAND: <<{0}>>", command));
            logger.debug("RESULT: " + output.toString());

            process.waitFor();
            logger.debug("exit: " + process.exitValue());


        } catch (Throwable e) {
            throw new ApplicationException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return !output.toString().equals("");

    }

    @Override
    @ManagedOperation(description = "")
    public byte[] getRealSphinxConf(String dirName) throws IOException {
        String filePath = baseDir + "cfg/searching/" + dirName + "/sphinx.conf";
        logger.info("ABOUT TO RETRIEVE REAL SPHINX CONF: " + filePath);
        byte [] result = null;
        File file = new File(filePath);
        if(file.exists()) {
            Scanner scanner = new Scanner(new FileInputStream(file));
            StringBuilder text = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while ( scanner.hasNextLine() ){
                text.append(scanner.nextLine() + ls);
            }
            result = text.toString().getBytes("UTF-8");
        }
        return result;
    }

	@Override
	@ManagedOperation(description = "")
	public byte[] getSphinxLog(String dirName, Long recordNumber) throws IOException {
		String filePath = baseDir + "log/searching/" + dirName + "/searchd.log";
        logger.info("ABOUT TO RETRIEVE REAL SPHINX LOG: " + filePath);
        byte [] result = null;
       /* File file = new File(filePath);
        if(file.exists()) {
            Scanner scanner = new Scanner(new FileInputStream(file));
            StringBuilder text = new StringBuilder();
            String ls = System.getProperty("line.separator");
            
            long line = 0;
            
            while ( scanner.hasNextLine() && (line <= recordNumber || recordNumber == -1)){
                text.append(scanner.nextLine() + ls);
                line++;
            }
            result = text.toString().getBytes("UTF-8");
        }*/
        
        
        StringBuilder text = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec("tail -" + recordNumber + " " + filePath);
            java.io.BufferedReader input = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = null;
            String ls = System.getProperty("line.separator");
	        while((line = input.readLine()) != null) {
	                text.append(line + ls);
	        }
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
        }
        result = text.toString().getBytes("UTF-8");

        
        return result;
    }

    @Override
    @ManagedOperation(description = "")
    public Long getCollectionSize(Integer port, Integer searchdPort,  String collectionName) {
        return sphinxQLService.getCollectionSize(port, searchdPort, collectionName);
    }

    @Override
    @ManagedOperation(description = "")
    public Boolean runQuery(Integer searchdPort, String collectionName) {
        return sphinxQLService.runQuery(searchdPort, collectionName);
    }

    @Override
    @ManagedOperation(description = "")
    public SphinxQLMultiResult getSphinxQLConsoleResult(Integer searchdPort, String query) {
        return sphinxQLService.getSphinxQLMultyQueryResult(searchdPort, sphinxQLConsoleService.getConsoleQueries(query));
    }

    @Override
    @ManagedOperation(description = "")
    public Set<SearchQuery> getSearchQueriesResults(String collectionName, Long replicaNumber, Date lastParsedDate) {
        return queryLogService.getSearchQueriesResults(collectionName, replicaNumber, lastParsedDate);
    }

}
