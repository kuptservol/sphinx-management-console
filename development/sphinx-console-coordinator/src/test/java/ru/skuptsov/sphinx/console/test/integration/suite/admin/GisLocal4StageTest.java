package ru.skuptsov.sphinx.console.test.integration.suite.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by Developer on 02.06.2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

    ru.skuptsov.sphinx.console.test.integration.tests.GisLocal4StageTest.class

})
@ContextConfiguration(locations = {"classpath:sphinx.console-integration-test-plain-context.xml"})
public class GisLocal4StageTest {
}
