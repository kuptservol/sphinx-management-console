package ru.skuptsov.sphinx.console.coordinator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by lnovikova on 3/12/2015.
 */
@Target( { PARAMETER })
@Retention(RUNTIME)
public @interface ExistsInDB {

    Class entityClass() default Object.class;

    String fieldName() default "";

}
