package ru.skuptsov.sphinx.console.coordinator.task.command.db;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 18.08.14
 * Time: 0:42
 * To change this template use File | Settings | File Templates.
 *
 */
/*
Данный класс оставлен пока не решена проблема вложенных циклов в классах *State.java
 */
@Component("UPDATE_CONFIGURATION_INDEX")
@Scope("prototype")
public class UpdateIndexConfigurationCommand extends UpdateConfigurationCommand {
}
