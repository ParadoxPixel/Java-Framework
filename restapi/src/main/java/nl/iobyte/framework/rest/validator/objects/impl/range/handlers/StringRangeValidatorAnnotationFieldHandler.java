package nl.iobyte.framework.rest.validator.objects.impl.range.handlers;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.rest.validator.interfaces.IValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;
import nl.iobyte.framework.rest.validator.objects.impl.range.annotations.StringRangeRule;

public class StringRangeValidatorAnnotationFieldHandler implements IValidatorAnnotationFieldHandler<StringRangeRule> {

    @Override
    public void handle(Validator<?> validator, ValidatorField<?> field, StringRangeRule annotation) {
        if(!TypeConverter.isAssignable(String.class, field.getField().getRawType()))
            return;

        //noinspection unchecked
        ValidatorField<String> intField = (ValidatorField<String>) field;
        intField.onTest((instance, str) -> switch(annotation.type()) {
            case MIN -> str.length() >= annotation.lower();
            case MAX -> str.length() <= annotation.upper();
            case RANGE -> str.length() >= annotation.lower() && str.length() <= annotation.upper();
        });
    }

}
