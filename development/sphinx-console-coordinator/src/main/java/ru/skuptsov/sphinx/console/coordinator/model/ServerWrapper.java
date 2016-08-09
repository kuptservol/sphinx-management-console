package ru.skuptsov.sphinx.console.coordinator.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class ServerWrapper implements Serializable {

    @Valid
    private List<AdminProcess> adminProcesses;

    @NotNull
    @Valid
	private Server server;

    public List<AdminProcess> getAdminProcesses() {
        return adminProcesses;
    }

    public void setAdminProcesses(List<AdminProcess> adminProcesses) {
        this.adminProcesses = adminProcesses;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
