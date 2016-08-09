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
public class SphinxQLMultiResult implements Serializable {

    private static final long serialVersionUID = 5718349558157838178L;

    private List<SphinxQLResult> resultList = new ArrayList<SphinxQLResult>();
    int resultListSize;

    Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<SphinxQLResult> getResultList() {
        return resultList;
    }

    public void setResultList(List<SphinxQLResult> resultList) {
        this.resultList = resultList;
    }

    public int getResultListSize() {
        return resultList.size();
    }
}
