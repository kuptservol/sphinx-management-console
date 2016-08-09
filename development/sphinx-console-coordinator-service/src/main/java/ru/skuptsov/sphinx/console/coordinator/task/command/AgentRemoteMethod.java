package ru.skuptsov.sphinx.console.coordinator.task.command;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: lnovikova
 * Date: 2/4/15
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentRemoteMethod {
    private Method method;
    private Object[] arguments;
    boolean canBeExecuted = true;

    public AgentRemoteMethod(Method method, Object[] arguments) {
        this.method = method;
        this.arguments = arguments;
    }
    
    public AgentRemoteMethod(Method method, Object[] arguments, boolean canBeExecuted) {
        this.method = method;
        this.arguments = arguments;
        this.canBeExecuted = canBeExecuted;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArguments() {
        return arguments;
    }

	public boolean canBeExecuted() {
		return canBeExecuted;
	}

}
