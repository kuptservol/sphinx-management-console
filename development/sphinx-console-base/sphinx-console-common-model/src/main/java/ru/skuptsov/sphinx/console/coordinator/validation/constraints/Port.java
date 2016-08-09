package ru.skuptsov.sphinx.console.coordinator.validation.constraints;

import org.hibernate.validator.constraints.Range;
import ru.skuptsov.sphinx.console.coordinator.validation.validator.IpValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by lnovikova on 3/12/2015.
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Range(min=1, max=65535)
@ReportAsSingleViolation
public @interface Port {

    String message() default "{sphinx.console.validation.constraints.Port.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};
}
