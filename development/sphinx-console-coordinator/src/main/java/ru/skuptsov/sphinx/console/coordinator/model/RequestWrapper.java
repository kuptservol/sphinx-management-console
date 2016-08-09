package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;

public class RequestWrapper<T> implements Serializable {

	private T parameter;

	public T getParameter() {
	    return parameter;
	}

    public void setParameter(T parameter) {
        this.parameter = parameter;
    }
}
