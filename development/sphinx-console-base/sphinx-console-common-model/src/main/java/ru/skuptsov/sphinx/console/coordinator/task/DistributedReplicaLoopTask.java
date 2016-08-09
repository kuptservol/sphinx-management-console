package ru.skuptsov.sphinx.console.coordinator.task;

import java.util.List;

import ru.skuptsov.sphinx.console.coordinator.model.Replica;

public abstract class DistributedReplicaLoopTask extends DistributedTask {

    private Long maxNumber;
//    private Long minNumber;
    private List<Replica> replicas;

    public void initReplicaLoop(List<Replica> replicas) {
        this.replicas = replicas;
        if(replicas.size() > 0 )  {
            maxNumber = replicas.get(replicas.size() - 1).getNumber();
            setReplicaNumber(replicas.get(0).getNumber());
        }
    }

    public boolean hasNext() {
        return getReplicaNumber() != null && maxNumber != null && getReplicaNumber() < maxNumber;
    }


    public void next() {
        for (int i = 0; i < replicas.size(); i++) {
            if(replicas.get(i).getNumber().equals(getReplicaNumber())) {
                setReplicaNumber      (replicas.get(i + 1).getNumber());
                setSearchServer       (replicas.get(i + 1).getSearchProcess().getServer());
                setSearchAgentAddress (replicas.get(i + 1).getSearchAgentAddress());
                setSearchConfiguration(replicas.get(i + 1).getSearchProcess().getConfiguration());
                setSearchConfigurationPort(Integer.parseInt(replicas.get(i + 1).getSearchProcess().getConfiguration().getSearchListenPort()));
                setDistributedConfigurationPort(replicas.get(i + 1).getSearchProcess().getConfiguration().getDistributedListenPort());

                break;
            }
        }
    }

    public void first() {
        setReplicaNumber(replicas.get(0).getNumber());
        setSearchServer(replicas.get(0).getSearchProcess().getServer());
        setSearchAgentAddress(replicas.get(0).getSearchAgentAddress());
        setSearchConfiguration(replicas.get(0).getSearchProcess().getConfiguration());
        setSearchConfigurationPort(Integer.parseInt(replicas.get(0).getSearchProcess().getConfiguration().getSearchListenPort()));
        setDistributedConfigurationPort(replicas.get(0).getSearchProcess().getConfiguration().getDistributedListenPort());
    }
}
