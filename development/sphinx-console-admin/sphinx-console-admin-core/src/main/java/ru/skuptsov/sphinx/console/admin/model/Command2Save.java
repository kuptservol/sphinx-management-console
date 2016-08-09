package ru.skuptsov.sphinx.console.admin.model;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import java.util.Date;

/**
 * Created by Developer on 07.05.2015.
 */
public class Command2Save extends BaseEntity {
    private Long id;
    private String commandId;
    private String methodName;
    private String jsonFilePath;
    private Date executeDate;

    public Command2Save() {
    }

    public Command2Save(String changesetName, ConcreteCommand command) {
        commandId = command.getId();
        methodName = command.getMethodName();
        jsonFilePath = command.getJsonFilePath();
        executeDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getJsonFilePath() {
        return jsonFilePath;
    }

    public void setJsonFilePath(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public Date getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(Date executeDate) {
        this.executeDate = executeDate;
    }
}
