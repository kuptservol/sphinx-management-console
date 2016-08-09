package ru.skuptsov.sphinx.console.coordinator.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Andrey on 07.08.2014.
 */
public enum SphinxProcessType {
    SEARCHING, INDEXING, FULL_INDEXING;

    public static SphinxProcessType getFirst(){
        return SphinxProcessType.values()[0];
    }

    public static SphinxProcessType getNext(SphinxProcessType curType){
        SphinxProcessType nextType = null;
        List<SphinxProcessType> valuesList = Arrays.asList(SphinxProcessType.values());
        int curIndex = valuesList.indexOf(curType);
        if(curIndex < valuesList.size()-1){
            nextType = valuesList.get(curIndex + 1);
        }
        return nextType;
    }
}
