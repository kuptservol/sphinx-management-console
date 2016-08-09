package ru.skuptsov.sphinx.console.admin.model;

/**
 * Created by Developer on 25.03.2015.
 */
public class CommandResult {
    private String jsonString;
    private String taskUid;
    private Integer code;
    private String activeLogQueryStatus;

    public CommandResult() {
    }

    public CommandResult(String jsonString, String taskUid, Integer code) {
        this.jsonString = jsonString;
        this.taskUid = taskUid;
        this.code = code;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getTaskUid() {
        return taskUid;
    }

    public void setTaskUid(String taskUid) {
        this.taskUid = taskUid;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getActiveLogQueryStatus() {
        return activeLogQueryStatus;
    }

    public void setActiveLogQueryStatus(String activeLogQueryStatus) {
        this.activeLogQueryStatus = activeLogQueryStatus;
    }
}
