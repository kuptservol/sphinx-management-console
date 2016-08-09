package ru.skuptsov.sphinx.console.validation.fullfillment;

import org.junit.Test;
import ru.skuptsov.sphinx.console.coordinator.model.search.query.SearchQueryHistorySearchParameters;
import ru.skuptsov.sphinx.console.validation.FullfillmentValidationTest;

/**
 * Created by lnovikova on 3/11/2015.
 */
public class SearchQueryHistorySearchParametersTest extends FullfillmentValidationTest {

    @Test
    public void test(){
        SearchQueryHistorySearchParameters targetObject = new SearchQueryHistorySearchParameters();

        checkInitErrors(targetObject);

        checkValidationErrorNotFixed(targetObject, "collectionName", "");

        checkValidationErrorFixed(targetObject, "collectionName", "col_name");

        checkValidationErrorNotFixed(targetObject, "query", "");

        checkValidationErrorFixed(targetObject, "query", "some query");

        checkValidationErrorNull(targetObject);
    }

}
