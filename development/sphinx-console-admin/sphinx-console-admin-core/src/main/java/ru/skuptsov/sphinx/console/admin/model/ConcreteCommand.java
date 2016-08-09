package ru.skuptsov.sphinx.console.admin.model;

import javax.xml.bind.annotation.*;

/**
 * Created by Developer on 20.03.2015.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "command")
public class ConcreteCommand implements Command {
    @XmlAttribute(required = true)
    private String id;
    @XmlElement(required = true)
    private String methodName;
    @XmlElement
    private String jsonFilePath;
    @XmlElement
    private String type = "POST";
    @XmlElement
    private Boolean runAlways = Boolean.FALSE;
    @XmlElement
    private Boolean ignoreErrors = Boolean.FALSE;
    @XmlElementRefs({
            @XmlElementRef(name="rollback", type = RollbackCommand.class),
            @XmlElementRef(name="rollbackChangeset", type = RollbackChangeset.class)
    })
    private Command rollback;

    public ConcreteCommand() {
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getJsonFilePath() {
        return jsonFilePath;
    }

    public void setJsonFilePath(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public Command getRollback() {
        return rollback;
    }

    public void setRollback(Command rollback) {
        this.rollback = rollback;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIgnoreErrors() {
        return ignoreErrors;
    }

    public void setIgnoreErrors(Boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }

    public Boolean getRunAlways() {
        return runAlways;
    }

    public void setRunAlways(Boolean runAlways) {
        this.runAlways = runAlways;
    }
}
