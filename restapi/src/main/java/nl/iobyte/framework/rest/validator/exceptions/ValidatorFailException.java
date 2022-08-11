package nl.iobyte.framework.rest.validator.exceptions;

import nl.iobyte.framework.generic.reflections.TypeConverter;

public class ValidatorFailException extends Exception {

    public ValidatorFailException(Object obj, ValidationRuleFailException exception) {
        super(
                TypeConverter.normalise(obj, String.class) + " didn't pass test: " + exception.getMessage(),
                exception.getCause()
        );
    }

}
