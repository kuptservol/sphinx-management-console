package ru.skuptsov.sphinx.console.coordinator.validation.constraints;

import ru.skuptsov.sphinx.console.coordinator.validation.validator.IpValidator;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Created by lnovikova on 3/12/2015.
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = IpValidator.class)
public @interface Ip {

    String message() default "{sphinx.console.validation.constraints.Ip.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};

}
