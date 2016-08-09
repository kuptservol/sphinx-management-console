package ru.skuptsov.sphinx.console.coordinator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ParallelSubflow {
	String name() default "";
	boolean start() default false;
	boolean end() default false;
}



