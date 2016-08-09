package ru.skuptsov.sphinx.console.coordinator.model;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.Serializable;
import java.text.MessageFormat;

public class Status implements Serializable {
    private int code;
    private String message;
    private String description;
    private SystemInterface systemInterface;
    private String stackTrace;
    private String taskUID;


    public static final int SUCCESS_CODE = 0;
    public static final int FAILURE_LOCAL_DB_CODE = 1;
    public static final int BAD_REST_REQUEST = 2;

    public Status() {

    }

    public Status (SystemInterface systemInterface) {
        this.systemInterface = systemInterface;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public SystemInterface getSystemInterface() {
        return systemInterface;
    }

    public void setSystemInterface(SystemInterface systemInterface) {
        this.systemInterface = systemInterface;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getTaskUID() {
        return taskUID;
    }

    public void setTaskUID(String taskUID) {
        this.taskUID = taskUID;
    }

    public enum SystemInterface {
        COORDINATOR_CONFIGURATION,
        COORDINATOR_VIEW,
        COORDINATOR_CALLBACK,
        COORDINATOR_AGENT,
        COORDINATOR_MONITORING,
        COORDINATOR_DB,
        MERGE_ACTION_DB;
    }

    public enum StatusCode {
        SUCCESS_CODE(0, "success", "success"),
        FAILURE_LOCAL_DB_CODE(1, "failure", "error occured while executing operation on coordinator DB"),
        BAD_REST_REQUEST(2, "failure", "Could not read JSON: No content to map due to end-of-input"),
        BAD_COORDINATOR_CALLBACK_ADDRESS(3, "failure", "error occured while setting coordinator callback address"),
        FAILURE_EXECUTE_COORDINATOR_CALLBACK(4, "failure", "error occured while executing coordinator callback"),
        FAILURE_EXECUTE_SPHINX_COMMAND(5, "failure", "error occured while executing commands related to sphinx"),
        DATA_INTEGRITY_FAILURE(6, "failure", "data is incorrect"),
        DB_CONNECTION_FAILURE(7, "failure", "could not connect to DB"),
        AGENT_SERVICE_UNAVAILABLE(8, "failure", "could not connect to agent service"),
        INDEXING_CANNOT_BE_EXECUTED(9, "failure", "cannot execute indexing"),
        MERGE_CANNOT_BE_EXECUTED(10, "failure", "cannot execute merge"),
        STOP_MERGE_CANNOT_BE_EXECUTED(11, "failure", "cannot execute stop merge"),
        MERGE_ACTION_FAILURE(12, "failure", "cannot execute merge action"),
        FAILURE_COORDINATOR_COMMAND(13, "failure", "error occured while executing coordinator command"),
        REQUEST_PARAM_VALIDATION_FAILED(14, "failure", "not valid JSON parameters"),
        ILLEGAL_FULL_INDEXING_APPLY_ACTION(15, "failure", "illegal full indexing apply action, full indexing state not in(READY_FOR_APPLY, ERROR_APPLY, OK)"),
        FAILURE_REPEATED_TASK(16, "failure", "Same type task for this collection already running. Wait until previous task will finish or stop it manually"),
        FAILURE_SPHINX_QL_COMMAND(17, "failure", "sphinxQL command failure"),
        REBUILD_SNIPPET_CANNOT_BE_EXECUTED(18, "failure", "cannot execute rebuild snippet");


        private int code;
        private String title;
        private String description;

        StatusCode(int code, String title, String description) {
            this.code = code;
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public static Status build(SystemInterface systemInterface, StatusCode statusCode) {

        Status status = new Status(systemInterface);
        status.setCode(statusCode.getCode());
        status.setDescription(statusCode.getDescription());
        status.setMessage(statusCode.getTitle());

        return status;
    }

    public static Status build(SystemInterface systemInterface, StatusCode statusCode, String taskUID) {

        Status status = build(systemInterface, statusCode);
        status.setTaskUID(taskUID);

        return status;
    }

    public static Status build(SystemInterface systemInterface, StatusCode statusCode, String taskUID, String message) {

        Status status = build(systemInterface, statusCode, taskUID);
        status.setMessage(message);

        return status;
    }

    public static Status build(SystemInterface systemInterface, StatusCode statusCode, String taskUID, Throwable e) {

        Status status = build(systemInterface, statusCode, taskUID);
        status.addExceptionData(e);

        return status;
    }

    public static Status build(SystemInterface systemInterface, StatusCode statusCode, Throwable e) {

        Status status = build(systemInterface, statusCode);
        status.addExceptionData(e);

        return status;
    }

    private void addExceptionData(Throwable e){
        this.setMessage(e.getMessage());
        this.setStackTrace(ExceptionUtils.getFullStackTrace(e));
        //ExceptionUtils.getRootCauseMessage(e)
    }

/*
    //TODO refactor params
    public static Status build(SystemInterface systemInterface, StatusCode statusCode, String... params) {
        Status status = new Status(systemInterface);
        status.setCode(statusCode.getCode());
        status.setMessage(statusCode.getTitle());
        status.setDescription(statusCode.getDescription());
        if(params != null && params.length != 0){
            status.setStackTrace(params[0]);
        }
        if(params != null && params.length > 1){
            status.setTaskUID(params[1]);
        }
        return status;
    }
*/
    public Status addMessage(String message){
        this.setMessage(message);

        return this;
    }

    @Override
    public String toString() {
        return MessageFormat.format("code: {0}, message: {1}, description: {2}, system interface: {3}, stack trace: {4}", getCode(), getMessage(), getDescription(), getSystemInterface(), getStackTrace());
    }

}
