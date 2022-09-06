package nl.iobyte.framework.rest.validator.objects.impl.string.handlers;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.rest.validator.interfaces.IValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;
import nl.iobyte.framework.rest.validator.objects.impl.string.annotations.StringCaseRule;
import nl.iobyte.framework.rest.validator.objects.impl.string.enums.StringCase;

import java.util.Locale;

public class StringCaseValidatorAnnotationFieldHandler implements IValidatorAnnotationFieldHandler<StringCaseRule> {

    @Override
    public void handle(Validator<?> validator, ValidatorField<?> field, StringCaseRule annotation) {
        if(!TypeConverter.isAssignable(String.class, field.getField().getRawType()))
            return;

        //noinspection unchecked
        ValidatorField<String> strField = (ValidatorField<String>) field;
        strField.onTest((instance, i) -> {
            if(i == null || i.isEmpty())
                return true;

            String strCased = toCase(i, annotation.type());
            if(annotation.transform()) {
                strField.getField().setValue(instance, strCased);
                return true;
            }

            return i.equals(strCased);
        });
    }

    /**
     * Transform string to specified case
     *
     * @param str  to transform
     * @param type of case
     * @return transformed string
     */
    private static String toCase(String str, StringCase type) {
        return switch(type) {
            case UPPER -> str.toUpperCase(Locale.ROOT);
            case LOWER -> str.toLowerCase(Locale.ROOT);
        };
    }

}
