package ru.skuptsov.sphinx.console.coordinator.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 21.08.14
 * Time: 22:20
 * To change this template use File | Settings | File Templates.
 */
public class MoveProcessToServerWrapper {
    @NotNull
    @Valid
    private Server newServer;
    @NotNull
    @Valid
    private SphinxProcess searchSphinxProcess;

    public Server getNewServer() {
        return newServer;
    }

    public void setNewServer(Server newServer) {
        this.newServer = newServer;
    }

    public SphinxProcess getSearchSphinxProcess() {
        return searchSphinxProcess;
    }

    public void setSearchSphinxProcess(SphinxProcess searchSphinxProcess) {
        this.searchSphinxProcess = searchSphinxProcess;
    }
}
