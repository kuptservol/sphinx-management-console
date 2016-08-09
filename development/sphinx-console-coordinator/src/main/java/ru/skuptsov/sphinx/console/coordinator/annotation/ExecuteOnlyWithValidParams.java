package ru.skuptsov.sphinx.console.coordinator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by lnovikova on 3/12/2015.
 */
@Target( { METHOD })
@Retention(RUNTIME)
public @interface ExecuteOnlyWithValidParams {

}
