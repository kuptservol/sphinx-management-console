package ru.skuptsov.sphinx.console.snippet.model;

import java.util.LinkedList;
import java.util.List;

public class Snippet {
    private Long id;
    
    private List<Item> items = new LinkedList<Item>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
    
    
}
