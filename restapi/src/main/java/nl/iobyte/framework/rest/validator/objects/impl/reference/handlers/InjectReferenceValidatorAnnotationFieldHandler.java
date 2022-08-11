package nl.iobyte.framework.rest.validator.objects.impl.reference.handlers;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.data.model.ModelService;
import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.source.IModelSourceStrategy;
import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.rest.validator.interfaces.IValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;
import nl.iobyte.framework.rest.validator.objects.impl.reference.annotations.InjectReferenceRule;

public class InjectReferenceValidatorAnnotationFieldHandler implements IValidatorAnnotationFieldHandler<InjectReferenceRule> {

    @Override
    public void handle(Validator<?> validator, ValidatorField<?> field, InjectReferenceRule annotation) {
        ReflectedField<? extends IModel> modelField = validator.getType().getField(
                annotation.modelField(),
                annotation.model()
        );

        FW.service(ModelService.class).getOptionalWrapper(annotation.model()).ifPresent(model -> {
            // noinspection unchecked
            ValidatorField<Object> keyField = (ValidatorField<Object>) field;
            keyField.onTest((instance, key) -> {
                if(key == null && annotation.required())
                    return false;

                try {
                    //noinspection unchecked
                    IModel value = ((IModelSourceStrategy<IModel>) model.getSourceStrategy(
                            IModelSourceStrategy.class
                    )).get(TypeConverter.normalise(
                            key,
                            model.getKeyField().getRawType()
                    ));
                    if(value == null && annotation.required())
                        return false;

                    modelField.setRawValue(instance, value);
                    return true;
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

}
