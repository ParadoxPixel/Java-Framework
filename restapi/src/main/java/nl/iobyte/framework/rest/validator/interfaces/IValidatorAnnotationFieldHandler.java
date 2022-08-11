package nl.iobyte.framework.rest.validator.interfaces;

import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;

import java.lang.annotation.Annotation;

public interface IValidatorAnnotationFieldHandler<T extends Annotation> {

    /**
     * Handle annotation for field
     *
     * @param validator  instance
     * @param field      instance
     * @param annotation instance
     */
    void handle(Validator<?> validator, ValidatorField<?> field, T annotation);

    /**
     * Handle annotation for field with type casting
     *
     * @param validator  instance
     * @param field      instance
     * @param annotation instance
     */
    default void handleRaw(Validator<?> validator, ValidatorField<?> field, Object annotation) {
        //noinspection unchecked
        handle(validator, field, (T) annotation);
    }

}
