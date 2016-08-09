package ru.skuptsov.sphinx.console.sphinx.service.api;

import java.io.IOException;

public interface SphinxService {
	void copyAndRenameFiles(String processName, String indexFileName) throws IOException;
    void copyIndexFiles(String indexFileName, String fromServer, String toServer, String fromDir, String toDir, String indexType, boolean isStrictCopy) throws IOException;
	void copyAndRenameFiles(String fromDir, String toDir, String oldProcessName, String newProcessName) throws IOException;
	void scpAndRenameFiles(String fromDir, String toDir, String processName) throws IOException;

    void executeAddProcessToStartupCommand(String processName) throws IOException;

    void executeRemoveProcessFromStartupCommand(String processName) throws IOException;

    void copyAndRenameFiles(String processName, String fromServer, String toServer) throws IOException;
    void executeRotatingCommand(String processName) throws IOException;
    void updateConfig(String processName, String content) throws IOException;
    void executeIndexerCommand(String processName, String collectionName, String index) throws IOException;
    void executeStopIndexerCommand(String processName) throws IOException;
    void createProcessFile(String processName, String sphinxServiceContent) throws IOException;
    void deleteProcessFile(String processName) throws IOException;
    void startSphinxProcess(String processName) throws IOException;
    void stopSphinxProcess(String processName, boolean ignoreFailure) throws IOException;
    void createSphinxFolders(String processName) throws IOException;
    void deleteSphinxFolders(String processName) throws IOException;
    void cleaningIndexDataFolder(String processName) throws IOException;
    void deleteIndexDataFiles(String processName) throws IOException;
    void executeStartMergingCommand(String processName, String collectionName, String option) throws IOException;
    void deleteSnippetFolders(String processName) throws IOException;
    void snippetRSync(String processName, String fromServer, String toServer, String fromDir, String toDir) throws IOException;
}
