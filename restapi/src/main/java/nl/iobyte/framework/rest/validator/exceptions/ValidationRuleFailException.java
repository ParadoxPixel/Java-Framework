package nl.iobyte.framework.rest.validator.exceptions;

import nl.iobyte.framework.generic.reflections.TypeConverter;

public class ValidationRuleFailException extends Exception {

    public ValidationRuleFailException(Object rule, Object obj) {
        super(rule.getClass().getName() + " failed on object of value " + TypeConverter.normalise(obj, String.class));
    }

    public ValidationRuleFailException(Object rule, Object obj, Throwable cause) {
        super(
                rule.getClass().getName() + " failed on object of value " + TypeConverter.normalise(obj, String.class),
                cause
        );
    }

}
