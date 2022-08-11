package nl.iobyte.framework.rest.validator.objects.impl.reference.handlers;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.data.model.ModelService;
import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.source.IModelSourceStrategy;
import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.rest.validator.interfaces.IValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;
import nl.iobyte.framework.rest.validator.objects.impl.reference.annotations.ReferenceRule;

public class ReferenceValidatorAnnotationFieldHandler implements IValidatorAnnotationFieldHandler<ReferenceRule> {

    @Override
    public void handle(Validator<?> validator, ValidatorField<?> field, ReferenceRule annotation) {
        FW.service(ModelService.class).getOptionalWrapper(annotation.model()).ifPresent(model -> {
            // noinspection unchecked
            ValidatorField<Object> keyField = (ValidatorField<Object>) field;
            keyField.onTest((instance, key) -> {
                if(key == null && annotation.required())
                    return false;

                try {
                    //noinspection unchecked
                    return ((IModelSourceStrategy<IModel>) model.getSourceStrategy(
                            IModelSourceStrategy.class
                    )).has(TypeConverter.normalise(
                            key,
                            model.getKeyField().getRawType()
                    )) || !annotation.required();
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

}
