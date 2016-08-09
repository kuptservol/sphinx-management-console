package ru.skuptsov.sphinx.console.coordinator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DBTable implements Serializable {
    private String name;
    private String nameWithoutScheme;
    
    private List<DBTableColumn> columns = new ArrayList<DBTableColumn>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DBTableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DBTableColumn> columns) {
		this.columns = columns;
	}

	public String getNameWithoutScheme() {
		return nameWithoutScheme;
	}

	public void setNameWithoutScheme(String nameWithoutScheme) {
		this.nameWithoutScheme = nameWithoutScheme;
	}
    
    
}
