package ru.skuptsov.sphinx.console.coordinator.task.command;

public interface CommandStrategyFactory {
    Command getCommand(String commandName);
}
