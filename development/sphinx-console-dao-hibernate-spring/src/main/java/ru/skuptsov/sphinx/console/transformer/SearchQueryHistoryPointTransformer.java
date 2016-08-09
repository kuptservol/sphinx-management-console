package ru.skuptsov.sphinx.console.transformer;

import org.hibernate.transform.ResultTransformer;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryHistoryPoint;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by lnovikova on 18.09.2015.
 */
public class SearchQueryHistoryPointTransformer implements ResultTransformer{
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {

        BigDecimal[] values = new BigDecimal[tuple.length-1];
        for(int i = 1; i < tuple.length; i++){
            values[i-1] = new BigDecimal(tuple[i].toString());
        }
        return tuple[0] != null ? new SearchQueryHistoryPoint(
                new BigDecimal(tuple[0].toString()).multiply(new BigDecimal(1000)).longValue(), values
        ) : null;
    }

    @Override
    public List transformList(List collection) {
        collection.removeAll(Collections.singleton(null));
        return collection;
    }
}
