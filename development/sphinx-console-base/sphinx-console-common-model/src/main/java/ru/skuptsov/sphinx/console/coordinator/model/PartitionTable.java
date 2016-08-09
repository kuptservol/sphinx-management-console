package ru.skuptsov.sphinx.console.coordinator.model;

import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;

public class PartitionTable extends BaseEntity {
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
