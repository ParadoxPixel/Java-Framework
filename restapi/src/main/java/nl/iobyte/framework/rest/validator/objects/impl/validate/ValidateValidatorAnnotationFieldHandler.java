package nl.iobyte.framework.rest.validator.objects.impl.validate;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.rest.validator.ValidatorService;
import nl.iobyte.framework.rest.validator.annotations.Validate;
import nl.iobyte.framework.rest.validator.interfaces.IValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;

public class ValidateValidatorAnnotationFieldHandler implements IValidatorAnnotationFieldHandler<Validate> {

    @Override
    public void handle(Validator<?> validator, ValidatorField<?> field, Validate annotation) {
        //noinspection unchecked
        Validator<Object> fieldValidator = (Validator<Object>) FW.service(
                ValidatorService.class
        ).register(field.getField().getRawType());

        field.onTest((instance, obj) -> {
            if(obj == null)
                return true;

            return fieldValidator.test(obj);
        });
    }

}
