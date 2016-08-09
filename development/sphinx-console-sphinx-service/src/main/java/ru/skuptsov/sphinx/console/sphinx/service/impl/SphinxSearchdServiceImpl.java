package ru.skuptsov.sphinx.console.sphinx.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skuptsov.sphinx.console.sphinx.service.api.SphinxService;

import java.io.IOException;

@Component("searchd")
public class SphinxSearchdServiceImpl extends AbstractSphinxService implements SphinxService {

	@Value("${base.dir}")
	private String baseDir;
	
	@Value("${service.dir}")
    private String serviceDir;

    @Value("${os.user}")
    private String user;

    @Value("${indexer.work.dir}")
    private String workDir;
    
    @Value("${indexer.processname}")
    private String indexerName;

	@Override
    public void createProcessFile(String processName, String sphinxServiceContent) throws IOException {
        logger.info("Searchd createProcessFile start.");
        linuxAtomaryCommands.createServiceFile(processName, sphinxServiceContent);
        logger.info("Searchd createProcessFile end.");
    }

    @Override
    public void deleteProcessFile(String processName) throws IOException {
        linuxAtomaryCommands.deleteProcessFile(processName);
    }

    @Override
    public void executeIndexerCommand(String processName, String collectionName, String index) throws IOException {
        logger.info("SphinxSearchdService start.");
    	String indexName = collectionName + "_" + index;
		if (index != null && index.equals("all")) {
			indexName = "--" + index;
		}
        logger.info("SphinxSearchdService run. indexName - " + indexName);
        linuxAtomaryCommands.executeCommand(workDir + indexerName + " --config " + baseDir + "cfg/searching/" + processName + "/sphinx.conf " + indexName, true);
        logger.info("SphinxSearchdService end.");
    }

    @Override
    public void startSphinxProcess(String processName) throws IOException {
        linuxAtomaryCommands.executeProcess(processName);
    }

    @Override
    public void stopSphinxProcess(String processName, boolean ignoreFailure) throws IOException {
        linuxAtomaryCommands.destroyProcess(processName, ignoreFailure);
    }

    @Override
    public void createSphinxFolders(String processName) throws IOException {
        linuxAtomaryCommands.createDirs("data/searching/" + processName + ", " +
                                            "log/searching/" + processName + ", " +
                                                "pid/searching/" + processName + ", " +
                                                    "cfg/searching/" + processName + ", " +
                                                        "binlog/searching/" + processName + ", " +
                                                            "snippet/searching/" + processName);
    }

    @Override
    public void deleteSphinxFolders(String processName) throws IOException {
        linuxAtomaryCommands.deleteDirsOrFiles("data/searching/" + processName + ", " +
                                                    "log/searching/" + processName + ", " +
                                                        "pid/searching/" + processName + ", " +
                                                            "cfg/searching/" + processName + ", " +
                                                                "binlog/searching/" + processName + ", " +
                                                                    "snippet/searching/" + processName);
    }

    @Override
    public void cleaningIndexDataFolder(String processName) throws IOException {
        linuxAtomaryCommands.deleteDirOrFile("data/searching/" + processName);
    }

    @Override
    public void deleteIndexDataFiles(String processName) throws IOException {
        linuxAtomaryCommands.deleteAllFilesInDir("data/searching/" + processName);
    }

    @Override
    public void updateConfig(String processName, String content) throws IOException {
        linuxAtomaryCommands.replaceFile("cfg/searching/" + processName + "/sphinx.conf", content);
    }

    @Override
    public void executeRotatingCommand(String processName) throws IOException {
        /*https://jira.skuptsov.ru/browse/sphinx.console-645 Ротейт индексов не должен вызывать ошибку при выключенном searchd*/
        String command = sudoPath + " kill -s SIGHUP $(cat " + baseDir + "pid/searching/" + processName +  "/searchd.pid)";
        try{
            linuxAtomaryCommands.executeCommand(command);
        }catch (Exception e){
            logger.warn("Fail to execute rotate command: " + command + ". Ignore failure because process may not exist. In this case new files will be adjusted when searchd will start next time", e);
        }
    }

    @Override
    public void executeAddProcessToStartupCommand(String processName) throws IOException {
        linuxAtomaryCommands.addProcessToStartup(processName);
    }

    @Override
    public void executeRemoveProcessFromStartupCommand(String processName) throws IOException {
        linuxAtomaryCommands.removeProcessFromStartup(processName);
    }

    @Override
    public void copyAndRenameFiles(String processName, String fromServer, String toServer) throws IOException {
        scpAndRenameFiles(user + "@" + fromServer + ":" + baseDir + "data/searching/" + processName + "/",
                          user + "@" + toServer + ":" + baseDir + "data/searching/" + processName + "/",
                          processName);
    }

	@Override
	public void executeStopIndexerCommand(String processName) throws IOException {
		
	}

	@Override
	public void executeStartMergingCommand(String processName, String collectionName, String option) throws IOException {
		
	}

	@Override
	public void deleteSnippetFolders(String processName) throws IOException {
		linuxAtomaryCommands.deleteDirsOrFiles("snippet/searching/" + processName);	
	}

	@Override
	public void snippetRSync(String processName, String fromServer, String toServer, String fromDir, String toDir) throws IOException {
		
		
	}
}
