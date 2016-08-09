package ru.skuptsov.sphinx.console.coordinator.model;

import org.hibernate.validator.constraints.NotEmpty;
import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by Andrey on 20.11.2014.
 */
public class ExternalAction extends BaseEntity {
    private Long id;
    @NotNull
    private ExternalActionType type;
    @NotEmpty
    private String code;
    private DataSource dataSource;
    private Server server;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExternalActionType getType() {
        return type;
    }

    public void setType(ExternalActionType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
