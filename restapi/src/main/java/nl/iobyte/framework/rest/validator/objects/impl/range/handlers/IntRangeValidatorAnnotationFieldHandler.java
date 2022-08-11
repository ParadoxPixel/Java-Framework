package nl.iobyte.framework.rest.validator.objects.impl.range.handlers;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.rest.validator.interfaces.IValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;
import nl.iobyte.framework.rest.validator.objects.impl.range.annotations.IntRangeRule;

public class IntRangeValidatorAnnotationFieldHandler implements IValidatorAnnotationFieldHandler<IntRangeRule> {

    @Override
    public void handle(Validator<?> validator, ValidatorField<?> field, IntRangeRule annotation) {
        if(!TypeConverter.isAssignable(Integer.class, field.getField().getRawType()))
            return;

        //noinspection unchecked
        ValidatorField<Integer> intField = (ValidatorField<Integer>) field;
        intField.onTest((instance, i) -> switch(annotation.type()) {
            case MIN -> i >= annotation.lower();
            case MAX -> i <= annotation.upper();
            case RANGE -> i >= annotation.lower() && i <= annotation.upper();
        });
    }

}
