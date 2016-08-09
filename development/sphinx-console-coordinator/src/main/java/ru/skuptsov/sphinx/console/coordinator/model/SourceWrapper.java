package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;

/**
 * Created by Andrey on 12.12.2014.
 */
public class SourceWrapper implements Serializable {

    private DataSource datasource;
    private String mainSqlQuery;
    private String deltaSqlQuery;
    private String killSqlQuery;
    private String killFieldKey;
    private String killFieldValue;
    private String killType;
    private String tableName;

    public String getMainSqlQuery() {
        return mainSqlQuery;
    }

    public void setMainSqlQuery(String mainSqlQuery) {
        this.mainSqlQuery = mainSqlQuery;
    }

    public String getDeltaSqlQuery() {
        return deltaSqlQuery;
    }

    public void setDeltaSqlQuery(String deltaSqlQuery) {
        this.deltaSqlQuery = deltaSqlQuery;
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }

    public String getKillSqlQuery() {
        return killSqlQuery;
    }

    public void setKillSqlQuery(String killSqlQuery) {
        this.killSqlQuery = killSqlQuery;
    }

    public String getKillFieldKey() {
        return killFieldKey;
    }

    public void setKillFieldKey(String killFieldKey) {
        this.killFieldKey = killFieldKey;
    }

    public String getKillFieldValue() {
        return killFieldValue;
    }

    public void setKillFieldValue(String killFieldValue) {
        this.killFieldValue = killFieldValue;
    }

    public String getKillType() {
        return killType;
    }

    public void setKillType(String killType) {
        this.killType = killType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
