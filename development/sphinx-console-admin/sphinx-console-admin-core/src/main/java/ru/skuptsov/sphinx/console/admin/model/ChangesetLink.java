package ru.skuptsov.sphinx.console.admin.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Developer on 20.03.2015.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "changesetLink")
public class ChangesetLink implements Command {
    @XmlElement(required = true)
    private String changesetFilePath;

    public String getChangesetFilePath() {
        return changesetFilePath;
    }

    public void setChangesetFilePath(String changesetFilePath) {
        this.changesetFilePath = changesetFilePath;
    }
}
