package ru.skuptsov.sphinx.console.coordinator.model;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class SphinxProcess extends BaseEntity {
	
	private Long id;
    @NotNull
    @Valid
    private Server server;
    @NotNull
//    @Valid
    private Collection collection;
    @NotEmpty
    private String indexName;
    @NotNull
    @Valid
    private Configuration configuration;
    @Valid
    private Replica replica;
    @NotNull
    private SphinxProcessType type;
    @Valid
    private SphinxProcess deltaSphinxProcess;
    private byte[] configContent;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public SphinxProcessType getType() {
        return type;
    }

    public void setType(SphinxProcessType type) {
        this.type = type;
    }

    public byte[] getConfigContent() {
        return configContent;
    }

    public void setConfigContent(byte[] configContent) {
        this.configContent = configContent;
    }

    public Replica getReplica() {
        return replica;
    }

    public void setReplica(Replica replica) {
        this.replica = replica;
    }

    public SphinxProcess getDeltaSphinxProcess() {
        return deltaSphinxProcess;
    }

    public void setDeltaSphinxProcess(SphinxProcess deltaSphinxProcess) {
        this.deltaSphinxProcess = deltaSphinxProcess;
    }
}
