package ru.skuptsov.sphinx.console.admin.service.api;

import ru.skuptsov.sphinx.console.admin.model.CommandResult;

public interface RestService {
	 CommandResult callCoordinatorMethod(String methodUrl, String prefixJsonPath, String jsonFilePath, String jsonPropsFilePath, String type, boolean ignoreErrors);
	 void setCoordinatorPort(String coordinatorPort);
	 void setCoordinatorServer(String coordinatorServer);
}
