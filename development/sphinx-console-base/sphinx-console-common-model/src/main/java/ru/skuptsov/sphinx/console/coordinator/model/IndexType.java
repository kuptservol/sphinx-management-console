package ru.skuptsov.sphinx.console.coordinator.model;

import java.util.ArrayList;
import java.util.List;

public enum IndexType {
	
	DELTA("delta"),
	MAIN("main"),
    ALL("all");
	
	private String title;
    private static final List<IndexType> deltaAndMainList = new ArrayList<IndexType>();

    static
    {
        deltaAndMainList.add(IndexType.DELTA);
        deltaAndMainList.add(IndexType.MAIN);
    }

    private IndexType(String title) {
	    this.title = title;
	}

	public String getTitle() {
	    return title;
	}

    public static List<IndexType> getDeltaAndMainList() {
        return deltaAndMainList;
    }

    public static IndexType getByTitle(String title) {
        for (IndexType indexType : IndexType.values()) {
            if (indexType.getTitle().equals(title)) {
                return indexType;
            }
        }
        return null;
    }
}
