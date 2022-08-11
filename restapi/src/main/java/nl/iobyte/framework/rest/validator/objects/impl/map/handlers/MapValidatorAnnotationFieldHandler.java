package nl.iobyte.framework.rest.validator.objects.impl.map.handlers;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.rest.validator.annotations.ValidationRule;
import nl.iobyte.framework.rest.validator.interfaces.IValidatorAnnotationFieldHandler;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.ValidatorField;
import nl.iobyte.framework.rest.validator.objects.impl.map.annotations.MapRule;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MapValidatorAnnotationFieldHandler implements IValidatorAnnotationFieldHandler<MapRule> {

    @Override
    public void handle(Validator<?> validator, ValidatorField<?> field, MapRule annotation) {
        if(!TypeConverter.isAssignable(Map.class, field.getField().getRawType()))
            return;

        // noinspection unchecked
        ValidatorField<Map<Object, Object>> mapField = (ValidatorField<Map<Object, Object>>) field;
        mapField.onTest((instance, map) -> {
            if(map == null || map.isEmpty())
                return false;

            if(annotation.explicit())
                if(map.size() != annotation.keys().length)
                    return false;

            Map<String, Object> converted = ((Set<?>) map.keySet()).stream()
                                                                   .collect(Collectors.toMap(
                                                                           key -> {
                                                                               if(!TypeConverter.isAssignable(
                                                                                       annotation.keyType(),
                                                                                       key.getClass()
                                                                               ))
                                                                                   throw new RuntimeException(
                                                                                           "invalid key type of entry in map");

                                                                               String str = (String) TypeConverter.normalise(
                                                                                       key,
                                                                                       String.class
                                                                               );
                                                                               return annotation.caseSensitive() ? str : str.toLowerCase();
                                                                           },
                                                                           key -> {
                                                                               Object value = map.get(key);
                                                                               if(!TypeConverter.isAssignable(
                                                                                       annotation.valueType(),
                                                                                       value.getClass()
                                                                               ))
                                                                                   throw new RuntimeException(
                                                                                           "invalid value type of entry in map");

                                                                               return value;
                                                                           }
                                                                   ));

            return Arrays.stream(annotation.keys())
                         .map(key -> annotation.caseSensitive() ? key : key.toLowerCase())
                         .allMatch(key -> {
                             if(!converted.containsKey(key))
                                 return false;

                             return !annotation.notEmpty() || converted.get(key) != null;
                         });
        });
    }

    /**
     * Check if array contains validation rule
     *
     * @param annotations array of annotations
     * @return whether array has an validation rule
     */
    private static boolean hasValidationRule(Annotation[] annotations) {
        for(Annotation annotation : annotations)
            if(isValidationRule(annotation))
                return true;

        return false;
    }

    /**
     * Check if annotation is validation rule
     *
     * @param annotation to check
     * @return whether annotation is validation rule
     */
    private static boolean isValidationRule(Annotation annotation) {
        if(ValidationRule.class.equals(annotation.annotationType()))
            return true;

        for(Annotation a : annotation.annotationType().getAnnotations())
            if(isValidationRule(a))
                return true;

        return false;
    }

}
