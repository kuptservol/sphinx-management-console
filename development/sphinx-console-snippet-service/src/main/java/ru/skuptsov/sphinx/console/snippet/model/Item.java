package ru.skuptsov.sphinx.console.snippet.model;

public class Item {
    private String fieldName;
    private String value;
    
    public Item() {
    	
    }
    
    public Item(String fieldName, String value) {
    	this.fieldName = fieldName;
    	this.value = value;
    }
    
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
    
    
}
