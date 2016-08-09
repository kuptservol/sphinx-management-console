package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;

public class ResponseWrapper<T> implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 135673L;
	private T result;

    public ResponseWrapper() {
    }

    public ResponseWrapper(T result) {
	    this.result = result;
	}

	public T getResult() {
	    return result;
	}

}
