package ru.skuptsov.sphinx.console.admin.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by Developer on 19.03.2015.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Changeset {
    @XmlElementRefs({
            @XmlElementRef(name="command", type = ConcreteCommand.class),
            @XmlElementRef(name="changesetLink", type = ChangesetLink.class)
    })
    private List<Command> commands;

    @XmlTransient
    private String name;

    @XmlTransient
    private String path = "";

    @XmlElement
    private String jsonPropsFilePath;

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJsonPropsFilePath() {
        return jsonPropsFilePath;
    }

    public void setJsonPropsFilePath(String jsonPropsFilePath) {
        this.jsonPropsFilePath = jsonPropsFilePath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
