package ru.skuptsov.sphinx.console.sphinx.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

@Component("indexer")
public class SphinxIndexerServiceImpl extends AbstractSphinxService implements SphinxService {
    public static final String INDEXER_PATTERN = "{0}{1} --config {2}cfg/indexing/{3}/sphinx.conf";
    public static final String INDEXER_INDEX_PATTERN = INDEXER_PATTERN +" {4} | tee {2}log/indexing/{3}/indexer.log";
    public static final String INDEXER_MERGE_PATTERN = INDEXER_PATTERN +" --merge";
    public static final String INDEXER_MERGE_INDEX_PATTERN = INDEXER_MERGE_PATTERN +" {4}_main {4}_delta {5} | tee {2}log/indexing/{3}/indexer.log";

    @Value("${base.dir}")
	private String baseDir;

    @Value("${os.user}")
    private String user;
    
    @Value("${indexer.work.dir}")
    private String workDir;
    
    @Value("${indexer.processname}")
    private String indexerName;

	@Override
	public void executeIndexerCommand(String processName, String collectionName, String index) throws IOException {
        logger.info("SphinxIndexerService start.");
		String indexName = collectionName + "_" + index;
		if (index != null && index.equals("all")) {
			indexName = "--" + index;
		}

		if(! new File(workDir+indexerName).isFile())
			throw new IOException("Indexer binary not found on path :"+workDir+indexerName);

        logger.info("SphinxIndexerService run. Command - " + MessageFormat.format(INDEXER_INDEX_PATTERN, workDir, indexerName, baseDir, processName, indexName));
	    linuxAtomaryCommands.executeCommand(MessageFormat.format(INDEXER_INDEX_PATTERN, workDir, indexerName, baseDir, processName, indexName), true);
        logger.info("SphinxIndexerService end.");
	}

	@Override
	public void createProcessFile(String processName, String sphinxServiceContent) throws IOException {
        logger.info("Index empty createProcessFile.");
	}

	@Override
	public void deleteProcessFile(String processName) throws IOException {
	}

	@Override
	public void startSphinxProcess(String processName) throws IOException {
	    executeIndexerCommand(processName, null, "--all");
	}

	@Override
	public void stopSphinxProcess(String processName, boolean ignoreFailure) throws IOException {
    }

    @Override
	public void createSphinxFolders(String processName) throws IOException {
	    linuxAtomaryCommands.createDirs("data/indexing/" + processName + ", " +
                                            "log/indexing/" + processName + ", " +
                                                "pid/indexing/" + processName + ", " +
                                                    "cfg/indexing/" + processName + ", " +
                                                        "binlog/indexing/" + processName + ", " +
                                                            "snippet/indexing/" + processName);
	}

	@Override
	public void deleteSphinxFolders(String processName) throws IOException {
	    linuxAtomaryCommands.deleteDirsOrFiles("data/indexing/" + processName + ", " +
                                                    "log/indexing/" + processName + ", " +
	                                                    "pid/indexing/" + processName + ", " +
	                                                        "cfg/indexing/" + processName + ", " +
	                                                            "binlog/indexing/" + processName + ", " +
                                                                    "snippet/indexing/" + processName);
	}

    @Override
    public void cleaningIndexDataFolder(String processName) throws IOException {
    }

    @Override
    public void deleteIndexDataFiles(String processName) throws IOException {
        linuxAtomaryCommands.deleteAllFilesInDir("data/indexing/" + processName);
    }

    @Override
	public void updateConfig(String processName, String content) throws IOException {
	    linuxAtomaryCommands.replaceFile("cfg/indexing/" + processName + "/sphinx.conf", content);
	}

	@Override
	public void executeRotatingCommand(String processName) throws IOException {
        /*https://jira.skuptsov.ru/browse/sphinx.console-645 Ротейт индексов не должен вызывать ошибку при выключенном searchd*/
		String command = sudoPath + " kill -s SIGHUP $(cat " + baseDir + "pid/indexing/" + processName +  "/searchd.pid)";
		try{
			linuxAtomaryCommands.executeCommand(command);
		}catch (Exception e){
			logger.warn("Fail to execute rotate command: " + command + ". Ignore failure because process may not exist. In this case new files will be adjusted when searchd will start next time", e);
		}
	}

	@Override
    public void copyAndRenameFiles(String processName, String fromServer, String toServer) throws IOException {
        scpAndRenameFiles(user + "@" + fromServer + ":" + baseDir + "data/indexing/" + processName + "/",
                user + "@" + toServer + ":" + baseDir + "data/indexing/" + processName + "/",
                processName);
    }

    @Override
    public void executeStopIndexerCommand(String processName) throws IOException {
		// pkill return 1 if no process where found, so ignore this error, because indexer process can be finished already
		try{
			linuxAtomaryCommands.executeCommand("pkill -f  \"" + MessageFormat.format(INDEXER_PATTERN, workDir, indexerName, baseDir, processName, "") + "\"");
		}
		catch (Exception e){
			logger.info("Ignore error during executeStopIndexerCommand: " + e.getMessage());
		}
	}

	@Override
	public void executeStartMergingCommand(String processName, String collectionName, String option) throws IOException {
		//indexer --config /opt/sphinx/cfg/indexing/collection203_1/sphinx.conf --merge collection203_main collection203_delta --merge-dst-range deleted 0 0
		linuxAtomaryCommands.executeCommand(MessageFormat.format(INDEXER_MERGE_INDEX_PATTERN, workDir, indexerName, baseDir, processName, collectionName, option));
	}

	@Override
	public void deleteSnippetFolders(String processName) throws IOException {
		linuxAtomaryCommands.deleteDirsOrFiles("snippet/indexing/" + processName);	
	}

	@Override
	public void snippetRSync(String processName, String fromServer, String toServer, String fromDir, String toDir) throws IOException {
		linuxAtomaryCommands.rsync(fromServer, toServer, fromDir, toDir);
	}

	@Override
	public void executeAddProcessToStartupCommand(String processName) throws IOException {
	}

	@Override
	public void executeRemoveProcessFromStartupCommand(String processName) throws IOException {
	}
}
