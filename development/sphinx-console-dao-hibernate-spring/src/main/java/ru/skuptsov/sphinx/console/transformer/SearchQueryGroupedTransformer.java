package ru.skuptsov.sphinx.console.transformer;

import org.hibernate.transform.ResultTransformer;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryGrouped;

import java.util.Collections;
import java.util.List;

/**
 * Created by lnovikova on 18.09.2015.
 */
public class SearchQueryGroupedTransformer implements ResultTransformer{
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {

        return tuple[0] != null ? new SearchQueryGrouped(
                new Long(tuple[0].toString()),
                (String)tuple[1],
                (String)tuple[2],
                new Integer(tuple[3].toString()),
                new Integer(tuple[4].toString()),
                new Integer(tuple[5].toString()),
                new Integer(tuple[6].toString()),
                new Integer(tuple[7].toString()),
                new Integer(tuple[8].toString())
        ) : null;
    }

    @Override
    public List transformList(List collection) {
        collection.removeAll(Collections.singleton(null));
        return collection;
    }
}
