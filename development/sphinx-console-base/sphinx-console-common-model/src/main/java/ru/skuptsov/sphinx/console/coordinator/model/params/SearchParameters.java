package ru.skuptsov.sphinx.console.coordinator.model.params;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aefimov on 08.09.14.
 */
public class SearchParameters extends PageParameters {

    private List<Filter> filter;

    public List<Filter> getFilter() {
        return filter;
    }

    public void setFilter(List<Filter> filter) {
        this.filter = filter;
    }

    public String getValueFilterByName(String name){
        if(name==null||this.filter==null||this.filter.size()==0) return null;
        for (Filter filter : getFilter()){
            if(filter.getProperty()!=null&&filter.getProperty().length()>0&&filter.getProperty().equals(name))
                return filter.getValue();
        }
        return null;
    }

}
