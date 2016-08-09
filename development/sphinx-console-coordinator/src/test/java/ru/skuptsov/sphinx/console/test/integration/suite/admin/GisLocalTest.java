package ru.skuptsov.sphinx.console.test.integration.suite.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.skuptsov.sphinx.console.test.integration.tests.prepare.PrepareGisTest;

/**
 * Created by Developer on 02.06.2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

        PrepareGisTest.class,

        GisLocal4StageTest.class

})
public class GisLocalTest {
}
