package ru.skuptsov.sphinx.console.admin.service.api;

import ru.skuptsov.sphinx.console.admin.model.CommandResult;


/**
 * Created by Developer on 06.05.2015.
 */
public interface ChangesetService {
    public void rollback(String path);
    public void rollback(String path, String propsPath);
    public CommandResult execute(String path, boolean needSaveCommand);
    public CommandResult execute(String path, String propsPath, boolean needSaveCommand);
    public void set2File();
}
