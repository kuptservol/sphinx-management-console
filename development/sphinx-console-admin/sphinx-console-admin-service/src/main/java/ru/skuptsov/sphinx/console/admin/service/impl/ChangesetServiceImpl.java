package ru.skuptsov.sphinx.console.admin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skuptsov.sphinx.console.admin.model.*;
import ru.skuptsov.sphinx.console.admin.service.api.ChangesetService;
import ru.skuptsov.sphinx.console.admin.service.api.FileService;
import ru.skuptsov.sphinx.console.admin.service.api.RestService;
import ru.skuptsov.sphinx.console.admin.service.api.StringService;
import ru.skuptsov.sphinx.console.admin.spring.service.api.CommandService;
import ru.skuptsov.sphinx.console.admin.model.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Developer on 19.03.2015.
 */
@Service
public class ChangesetServiceImpl implements ChangesetService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileService fileService;

    @Autowired
    private RestService restService;

    @Autowired
    private StringService stringService;

    @Autowired
    private CommandService commandService;

    private Changeset getFromFile(String prefixChangesetPath, String changesetPath, String propsPath) {
        Changeset changeset = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Changeset.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            String changesetString = stringService.getFormattedString(prefixChangesetPath, changesetPath, propsPath);
            changeset = (Changeset) jaxbUnmarshaller.unmarshal(new StringReader(changesetString));
            changeset.setName(fileService.getFileName(changesetPath));

            logger.info("prefixChangesetPath - " + prefixChangesetPath + ", changesetPath - " + changesetPath + ", propsPath - " + propsPath + ".");
            changeset.setPath(prefixChangesetPath != null && !"".equals(prefixChangesetPath) ?
                                fileService.getFileDirPath(prefixChangesetPath + changesetPath) :
                                    fileService.getFileDirPath(changesetPath));
            logger.info("changeset.setPath - " + changeset.getPath() + ".");

            if((changeset.getJsonPropsFilePath() == null || "".equals(changeset.getJsonPropsFilePath())) &&
                    (propsPath != null && !"".equals(propsPath))) {
                changeset.setJsonPropsFilePath(fileService.isFileValid(prefixChangesetPath + propsPath) ?
                                                    prefixChangesetPath + propsPath : propsPath);
            }
            logger.info("changeset.setJsonPropsFilePath - " + changeset.getJsonPropsFilePath() + ".");

            logger.info("ChangesetServiceImpl success read changeset '" + changeset.getName() + "'.");
        } catch (Throwable e) {
            /*todo error*/
            logger.error("ChangesetServiceImpl JAXBContext exception.", e);
        }

        return changeset;
    }

    public void rollback(String changesetPath) {
        rollback(getFromFile("", changesetPath, null));
    }

    public void rollback(String changesetPath, String propsPath) {
        Changeset changeset = getFromFile("", changesetPath, propsPath);
        changeset.setJsonPropsFilePath(propsPath);
        rollback(getFromFile("", changesetPath, propsPath));
    }

    private CommandResult rollback(Changeset changeset) {
        CommandResult commandResult = null;

        Collections.reverse(changeset.getCommands());

        for (Command command : changeset.getCommands()) {
            if (command instanceof ConcreteCommand) {
                if (commandService.isExist(/*changeset.getName() + "\\" + */((ConcreteCommand) command).getId())) {
                    commandResult = rollback(changeset, (ConcreteCommand) command);
                }
            } else if (command instanceof ChangesetLink) {
                Changeset changesetLink = getFromFile(changeset.getPath(),
                                                      ((ChangesetLink) command).getChangesetFilePath(),
                                                      changeset.getJsonPropsFilePath());
                changeset.setName(changeset.getName() + "\\" + changesetLink.getName());
                commandResult = rollback(changesetLink);
            }
        }

        return commandResult;
    }

    private CommandResult rollback(Changeset rollbackChangeset, ConcreteCommand command) {
        CommandResult lastCommandResult = null;
        if(command.getRollback() != null) {
            if (command.getRollback() instanceof ConcreteCommand) {
                lastCommandResult = restService.callCoordinatorMethod(command.getMethodName(),
                                                                      rollbackChangeset.getPath(),
                                                                      command.getJsonFilePath(),
                                                                      rollbackChangeset.getJsonPropsFilePath(),
                                                                      command.getType(),
                                                                      command.getIgnoreErrors());
            } else if (command.getRollback() instanceof ChangesetLink) {
                Changeset changeset = getFromFile(rollbackChangeset.getPath(),
                                                  ((ChangesetLink) command.getRollback()).getChangesetFilePath(),
                                                  rollbackChangeset.getJsonPropsFilePath());
                changeset.setName(rollbackChangeset.getName() + "\\" + changeset.getName());
                lastCommandResult = execute(changeset, false);
            }
            if (lastCommandResult.getCode() == 0) {
                commandService.deleteByName(command.getId());
                logger.info("ChangesetService command execute delete from DB.");
            } else {
                logger.error("ChangesetService rollback failed. Output - '" + lastCommandResult.getJsonString() + "'.");
            }
        }

        return lastCommandResult;
    }

    public CommandResult execute(String changesetPath, boolean needSaveCommand) {
        return execute(getFromFile("", changesetPath, null), needSaveCommand);
    }

    public CommandResult execute(String changesetPath, String propsPath, boolean needSaveCommand) {
        Changeset changeset = getFromFile("", changesetPath, propsPath);
        if(changeset.getJsonPropsFilePath() == null || "".equals(changeset.getJsonPropsFilePath())) {
            changeset.setJsonPropsFilePath(propsPath);
        }
        logger.info("changeset.getJsonPropsFilePath - " + changeset.getJsonPropsFilePath() + ".");

        return execute(changeset, needSaveCommand);
    }

    public CommandResult execute(Changeset changeset, boolean needSaveCommand) {
        CommandResult lastCommandResult = null;
        for (Command command : changeset.getCommands()) {
            if(command instanceof ConcreteCommand) {
                if(!needSaveCommand || ((ConcreteCommand) command).getRunAlways() || !commandService.isExist(/*changeset.getName() + "\\" + */((ConcreteCommand)command).getId())) {
                    lastCommandResult = restService.callCoordinatorMethod(((ConcreteCommand) command).getMethodName(),
                                                                          changeset.getPath(),
                                                                          ((ConcreteCommand) command).getJsonFilePath(),
                                                                          changeset.getJsonPropsFilePath(),
                                                                          ((ConcreteCommand) command).getType(),
                                                                          ((ConcreteCommand) command).getIgnoreErrors());
                    if (lastCommandResult.getCode() != 0 && !((ConcreteCommand) command).getIgnoreErrors()) {

//                        TODO rollback here https://jira.skuptsov.ru/browse/sphinx.console-261
//                        rollback(repository, (ConcreteCommand)command);
                        throw new RuntimeException("RestUtils error on calling method - " + ((ConcreteCommand) command).getMethodName() + ".");
                    } else {
                        if(needSaveCommand) {
                            Command2Save command2Save = new Command2Save(changeset.getName(), (ConcreteCommand) command);
                            commandService.save(command2Save);
                        }
                    }
                } else {
                    logger.info("ChangesetService command already execute. Command name - " + changeset.getName() + "\\" + ((ConcreteCommand) command).getId() + ". Execute next.");
//                    throw new RuntimeException("ChangesetService command already execute. Command name - " + changeset.getName() + "\\" + ((ConcreteCommand)command).getId() + ".");
                }
            } else if(command instanceof ChangesetLink) {
                Changeset changesetLink = getFromFile(changeset.getPath(),
                                                      ((ChangesetLink)command).getChangesetFilePath(),
                                                      changeset.getJsonPropsFilePath());
                changeset.setName(changeset.getName() + "\\" + changesetLink.getName());
                lastCommandResult = execute(changesetLink, needSaveCommand);
            }
        }

        return lastCommandResult;
    }

    public void set2File() {
        List<Command> commands = new ArrayList<Command>();
        ChangesetLink link = new ChangesetLink();
        link.setChangesetFilePath("D:\\Work2\\sphinx.console-3-0\\development\\sphinx.console-admin\\sphinx.console-admin-service\\src\\main\\resources\\changesets\\addPlainCollection\\addPlainCollection.xml");
        commands.add(link);

        Changeset changeset = new Changeset();
        changeset.setCommands(commands);

        ConcreteCommand concreteCommand = new ConcreteCommand();
        concreteCommand.setMethodName("server/add");
        concreteCommand.setJsonFilePath("development\\sphinx.console-admin\\sphinx.console-admin-service\\src\\main\\resources\\changesets\\addServer\\json\\addServer.json");
        RollbackChangeset rollback = new RollbackChangeset();
//        ChangesetLink rollback = new ChangesetLink();
        rollback.setChangesetFilePath("development\\sphinx.console-admin\\sphinx.console-admin-service\\src\\main\\resources\\changesets\\deleteServer\\deleteServer.xml");
        concreteCommand.setRollback(rollback);

        changeset.getCommands().add(concreteCommand);

        try {
            File file = new File("D:\\Work2\\sphinx.console-3-0\\development\\sphinx.console-admin\\sphinx.console-admin-service\\src\\main\\resources\\changesets\\testChangesets.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Changeset.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(changeset, file);
            jaxbMarshaller.marshal(changeset, System.out);
        } catch (JAXBException e) {
            logger.error("ChangesetServiceImpl JAXBContext exception.", e);
        }
    }
}
