package ru.skuptsov.sphinx.console.coordinator.validation.validator;

import ru.skuptsov.sphinx.console.coordinator.validation.constraints.Ip;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by lnovikova on 3/12/2015.
 */
public class IpValidator implements ConstraintValidator<Ip, String> {

    private static final String ipRegexp = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Override
    public void initialize(Ip constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        return value.matches(ipRegexp);
    }
}
