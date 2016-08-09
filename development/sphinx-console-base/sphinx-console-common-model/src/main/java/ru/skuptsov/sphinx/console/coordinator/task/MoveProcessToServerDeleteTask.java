package ru.skuptsov.sphinx.console.coordinator.task;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 19.08.14
 * Time: 1:07
 * To change this template use File | Settings | File Templates.
 */
public class MoveProcessToServerDeleteTask extends DeleteProcessFromServerTask {

    @Override
    public Long getServerId() {
        return null;
    }

}
