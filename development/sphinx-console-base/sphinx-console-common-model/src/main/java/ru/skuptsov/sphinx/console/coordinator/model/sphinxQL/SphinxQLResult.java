package ru.skuptsov.sphinx.console.coordinator.model.sphinxQL;

import ru.skuptsov.sphinx.console.coordinator.model.Status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lnovikova
 * Date: 18.05.15
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class SphinxQLResult implements Serializable {

    private static final long serialVersionUID = 8669190646613175320L;

    private List<String> fields = new ArrayList<String>();
    private List<List<String>> resultList = new ArrayList<List<String>>();
    int resultListSize;

    Status status;


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addToResultList(List<String> row) {
        resultList.add(row);
    }

    public List<List<String>> getResultList() {
        return resultList;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public int getResultListSize() {
        return resultList.size();
    }

}
