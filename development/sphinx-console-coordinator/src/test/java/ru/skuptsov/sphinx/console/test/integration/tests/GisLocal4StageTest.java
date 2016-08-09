package ru.skuptsov.sphinx.console.test.integration.tests;

import org.junit.Test;
import ru.skuptsov.sphinx.console.test.integration.service.environment.helper.TestEnvironmentPlainCollectionHelper;

/**
 * Created by Developer on 02.06.2015.
 */
public class GisLocal4StageTest extends TestEnvironmentPlainCollectionHelper {
    @Test
//    @Ignore
    public void gisLocal4StageTest() throws Throwable {
        testExecutor.executeChangeset("D:\\Work2\\sphinx.console-3-0\\development\\sphinx.console-admin\\sphinx.console-admin-service\\src\\main\\resources\\changesets\\gis\\gis.xml");
    }
}
