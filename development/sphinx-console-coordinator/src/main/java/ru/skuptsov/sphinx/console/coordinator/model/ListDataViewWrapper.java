package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;

public class ListDataViewWrapper<T> implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 745767L;

	private Long total;
    
    private T list;

    public ListDataViewWrapper() {
    }
    
    public ListDataViewWrapper(Long total, T list) {
    	this.total = total;
    	this.list = list;
    }

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public T getList() {
		return list;
	}

	public void setList(T list) {
		this.list = list;
	}
    
    
}
