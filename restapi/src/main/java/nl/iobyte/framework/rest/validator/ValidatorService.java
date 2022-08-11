package nl.iobyte.framework.rest.validator;

import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.rest.validator.annotations.Validate;
import nl.iobyte.framework.rest.validator.annotations.ValidationRule;
import nl.iobyte.framework.rest.validator.interfaces.IValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;
import nl.iobyte.framework.rest.validator.objects.impl.empty.annotations.NotEmptyRule;
import nl.iobyte.framework.rest.validator.objects.impl.empty.handlers.NotEmptyValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.impl.map.annotations.MapRule;
import nl.iobyte.framework.rest.validator.objects.impl.map.handlers.MapValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.impl.range.annotations.*;
import nl.iobyte.framework.rest.validator.objects.impl.range.handlers.*;
import nl.iobyte.framework.rest.validator.objects.impl.reference.annotations.InjectReferenceRule;
import nl.iobyte.framework.rest.validator.objects.impl.reference.annotations.ReferenceRule;
import nl.iobyte.framework.rest.validator.objects.impl.reference.handlers.InjectReferenceValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.impl.reference.handlers.ReferenceValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.impl.validate.ValidateValidatorAnnotationFieldHandler;
import nl.iobyte.framework.structures.omap.ObjectMap;
import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ValidatorService extends ObjectMap<Class<?>, Validator<?>> implements Service {

    private final Map<Class<? extends Annotation>, IValidatorAnnotationFieldHandler<?>> annotationFieldHandlers;

    public ValidatorService() {
        this(ReflectedMap.getMapSupplier(HashMap.class));
    }

    public ValidatorService(IMapSupplier supplier) {
        annotationFieldHandlers = supplier.get();

        //Register range annotations
        on(DoubleRangeRule.class, new DoubleRangeValidatorAnnotationFieldHandler());
        on(FloatRangeRule.class, new FloatRangeValidatorAnnotationFieldHandler());
        on(IntRangeRule.class, new IntRangeValidatorAnnotationFieldHandler());
        on(LongRangeRule.class, new LongRangeValidatorAnnotationFieldHandler());

        //Register string annotations
        on(StringRangeRule.class, new StringRangeValidatorAnnotationFieldHandler());
        on(NotEmptyRule.class, new NotEmptyValidatorAnnotationFieldHandler());

        //Register map annotations
        on(MapRule.class, new MapValidatorAnnotationFieldHandler());

        //Register reference annotations
        on(ReferenceRule.class, new ReferenceValidatorAnnotationFieldHandler());
        on(InjectReferenceRule.class, new InjectReferenceValidatorAnnotationFieldHandler());

        //Register object annotations
        on(Validate.class, new ValidateValidatorAnnotationFieldHandler());
    }

    /**
     * Get model wrapper of type
     *
     * @param type of model
     * @param <T>  type of model
     * @return model wrapper instance
     */
    public <T> Validator<T> getValidator(Class<T> type) {
        //noinspection unchecked
        return (Validator<T>) get(type);
    }

    /**
     * Get optional model wrapper of type
     *
     * @param type of model
     * @param <T>  type of model
     * @return optional of model wrapper instance
     */
    public <T> Optional<Validator<T>> getOptionalValidator(Class<T> type) {
        return Optional.ofNullable(getValidator(type));
    }

    /**
     * Test object's validation rules if any
     *
     * @param obj object to test
     * @param <T> type of object
     * @return whether tests passed
     * @throws Exception thrown if a test fails
     */
    public <T> boolean test(T obj) throws Exception {
        //noinspection unchecked
        return test(obj, (Class<T>) obj.getClass());
    }

    /**
     * Test object's validation rules if any
     *
     * @param obj  object to test
     * @param type of object
     * @param <T>  type of object
     * @return whether tests passed
     * @throws Exception thrown if a test fails
     */
    public <T> boolean test(T obj, Class<T> type) throws Exception {
        Validator<T> validator = register(type);
        if(validator == null)
            return true;

        return validator.test(obj);
    }

    /**
     * Add handler for annotation field
     *
     * @param type                   of annotation
     * @param annotationFieldHandler handler
     * @param <T>                    type of annotation
     */
    public <T extends Annotation> void on(Class<T> type, IValidatorAnnotationFieldHandler<T> annotationFieldHandler) {
        annotationFieldHandlers.put(type, annotationFieldHandler);
    }

    /**
     * Get annotation field handler by type
     *
     * @param type of annotation
     * @param <T>  annotation type
     * @return model annotation field handler instance
     */
    public <T extends Annotation> IValidatorAnnotationFieldHandler<T> getAnnotationFieldHandler(Class<T> type) {
        //noinspection unchecked
        return (IValidatorAnnotationFieldHandler<T>) annotationFieldHandlers.get(type);
    }

    /**
     * Parse model of type
     *
     * @param type reflected type instance
     * @param <T>  type of model
     * @return model wrapper
     */
    public <T> Validator<T> register(Class<T> type) {
        if(containsKey(type))
            //noinspection unchecked
            return (Validator<T>) get(type);

        ReflectedType<T> reflectedType = ReflectedType.of(type);

        Validator<T> wrapper = new Validator<>(reflectedType);

        //Handle model fields
        reflectedType.getFields()
                     .stream()
                     .filter(field -> {
                         for(Annotation annotation : field.getAnnotations())
                             if(hasAnnotationOfType(annotation.annotationType(), ValidationRule.class))
                                 return true;

                         return false;
                     })
                     .forEach(field -> {
                         ValidatorField<?> validatorField = wrapper.addField(field);
                         for(Annotation annotation : field.getAnnotations()) {
                             if(!hasAnnotationOfType(annotation.annotationType(), ValidationRule.class))
                                 continue;

                             IValidatorAnnotationFieldHandler<?> handler = getAnnotationFieldHandler(annotation.annotationType());
                             if(handler == null)
                                 continue;

                             handler.handleRaw(wrapper, validatorField, annotation);
                         }
                     });

        if(wrapper.isEmpty())
            return null;

        put(wrapper);
        return wrapper;
    }

    /**
     * Check if annotation has annotation
     *
     * @param type           of annotation
     * @param annotationType annotation type to check for
     * @return whether annotation has annotation
     */
    private static boolean hasAnnotationOfType(
            Class<? extends Annotation> type,
            Class<? extends Annotation> annotationType
    ) {
        if(type.equals(annotationType))
            return true;

        for(Annotation annotation : type.getAnnotations()) {
            if(ignoredAnnotations.contains(annotation.annotationType()))
                continue;

            if(hasAnnotationOfType(annotation.annotationType(), annotationType))
                return true;
        }

        return false;
    }

    /**
     * List of annotations to ignore in hasAnnotationOfType
     */
    private static final Set<Class<? extends Annotation>> ignoredAnnotations = Set.of(
            Documented.class,
            Retention.class,
            Target.class
    );

}
