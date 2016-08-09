package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;

public class DBTableColumn implements Serializable {
    private String name;
    private String type;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    
    
}
