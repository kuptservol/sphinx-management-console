package ru.skuptsov.sphinx.console.coordinator.model;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.validation.constraints.Port;

import javax.validation.Valid;
import java.io.Serializable;

public class ReplicaName implements Serializable {

    private String serverName;
    private Integer port;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString(){
        return this.serverName != null ? (this.serverName + " : " + port) : null;
    }
}
