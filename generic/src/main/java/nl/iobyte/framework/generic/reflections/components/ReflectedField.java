package nl.iobyte.framework.generic.reflections.components;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.generic.reflections.objects.ReflectedAnnotation;

import java.lang.reflect.Field;

public class ReflectedField<T> extends ReflectedAnnotation {

    private final Field field;
    private final Class<T> type;

    public ReflectedField(Field field, Class<T> type) {
        super(field);
        field.trySetAccessible();

        this.field = field;
        this.type = type;
    }

    /**
     * Get field instance
     *
     * @return field instance
     */
    public Field getField() {
        return field;
    }

    /**
     * Get type of field
     *
     * @return type of field
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Get actual type of field
     *
     * @return type of field
     */
    public Class<?> getRawType() {
        return field.getType();
    }

    /**
     * Get name of field
     *
     * @return name of field
     */
    public String getName() {
        return field.getName();
    }

    /**
     * Check if field is assignable from type
     *
     * @param type to assign
     * @return if type can be assigned
     */
    public boolean isAssignable(Class<?> type) {
        return TypeConverter.isAssignable(field.getType(), type);
    }

    /**
     * Check if field is assignable from object
     *
     * @param obj to assign
     * @return if object can be assigned
     */
    public boolean isAssignable(Object obj) {
        return obj == null || isAssignable(obj.getClass());
    }

    /**
     * Get raw value of field
     *
     * @param obj instance to get field value from
     * @return raw field value
     * @throws IllegalArgumentException thrown while checking arguments
     * @throws IllegalAccessException   thrown while accessing field
     */
    public Object getRawValue(Object obj) throws IllegalAccessException {
        if(!field.getDeclaringClass().isInstance(obj))
            throw new IllegalArgumentException(
                    "invalid object type, expected: "
                            + field.getDeclaringClass().getName()
                            + " got: "
                            + obj.getClass().getName()
            );

        return field.get(obj);
    }

    /**
     * Get casted value of field
     *
     * @param obj instance to get field value from
     * @return casted field value
     * @throws IllegalAccessException thrown while getting value
     * @throws ClassCastException     thrown while casting
     */
    public T getValue(Object obj) throws IllegalAccessException, ClassCastException {
        return type.cast(getRawValue(obj));
    }

    /**
     * Set raw value of field
     *
     * @param obj   instance of
     * @param value of field
     * @throws IllegalAccessException   thrown while setting field value
     * @throws IllegalArgumentException thrown when object type or value type is incompatible
     */
    public void setRawValue(Object obj, Object value) throws IllegalAccessException, IllegalArgumentException {
        if(!field.getDeclaringClass().isInstance(obj))
            throw new IllegalArgumentException(
                    "invalid object type, expected: "
                            + field.getDeclaringClass().getName()
                            + " got: "
                            + obj.getClass().getName()
            );

        if(!isAssignable(value))
            throw new IllegalArgumentException(
                    "type " +
                            value.getClass().getName() +
                            " is not compatible with field type " +
                            field.getType().getName()
            );

        field.set(obj, value);
    }

    /**
     * Set raw value of field
     *
     * @param obj   instance of
     * @param value of field
     * @throws IllegalAccessException   thrown while setting field value
     * @throws IllegalArgumentException thrown when object type or value type is incompatible
     */
    public void setValue(Object obj, T value) throws IllegalAccessException, IllegalArgumentException {
        setRawValue(obj, value);
    }

    /**
     * Cast field to type
     *
     * @param type to cast to
     * @param <R>  type of field
     * @return reflected field instance
     */
    public <R> ReflectedField<R> castTo(Class<R> type) throws IllegalArgumentException {
        if(!TypeConverter.isAssignable(type, field.getType()))
            throw new IllegalArgumentException(
                    "field of type " +
                            getType().getName() +
                            " is not compatible with type " +
                            type.getName()
            );

        return ReflectedField.of(field, type);
    }

    /**
     * Get reflected field from field and type
     *
     * @param field instance of field
     * @param type  of field
     * @param <T>   type of field
     * @return reflected field instance
     */
    public static <T> ReflectedField<T> of(Field field, Class<T> type) {
        return new ReflectedField<>(field, type);
    }

}
