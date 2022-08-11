package nl.iobyte.framework.rest.validator.objects.impl.empty.handlers;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.rest.validator.interfaces.IValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;
import nl.iobyte.framework.rest.validator.objects.impl.empty.annotations.NotEmptyRule;

public class NotEmptyValidatorAnnotationFieldHandler implements IValidatorAnnotationFieldHandler<NotEmptyRule> {

    @Override
    public void handle(Validator<?> validator, ValidatorField<?> field, NotEmptyRule annotation) {
        if(TypeConverter.isAssignable(String.class, field.getField().getRawType())) {
            //noinspection unchecked
            ValidatorField<String> intField = (ValidatorField<String>) field;
            intField.onTest((instance, str) -> !(str == null || str.isEmpty() || str.isBlank()));
            return;
        }

        field.onTest(((instance, obj) -> obj != null));
    }

}
