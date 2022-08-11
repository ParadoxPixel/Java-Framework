package nl.iobyte.framework.generic.reflections.builders;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;

import java.lang.reflect.InvocationTargetException;

public class ReflectiveBuilder<T> {

    private final ReflectedType<T> type;
    private final T instance;

    public ReflectiveBuilder(ReflectedType<T> type, T instance) {
        this.type = type;
        this.instance = instance;
    }

    /**
     * Get reflected type of bui;der
     *
     * @return reflected type
     */
    public ReflectedType<T> getType() {
        return type;
    }

    /**
     * Set value of field with name
     *
     * @param name  of field
     * @param value of field
     * @return same instance
     * @throws IllegalAccessException   thrown while setting field value
     * @throws IllegalArgumentException thrown when field cannot be found or value is incompatible
     */
    public ReflectiveBuilder<T> set(String name, Object value) throws IllegalAccessException, IllegalArgumentException {
        ReflectedField<?> field = type.getFieldByName(name);
        if(!TypeConverter.normalise(field.getField().getType()).isInstance(value))
            throw new IllegalArgumentException(
                    "field with name \"" +
                            name +
                            "\" is not compatible with type " +
                            value.getClass().getName()
            );

        field.setRawValue(instance, value);
        return this;
    }

    /**
     * Set value of field with name and ignore exceptions
     *
     * @param name  of field
     * @param value of field
     * @return same instance
     */
    public ReflectiveBuilder<T> setIgnore(String name, Object value) {
        try {
            set(name, value);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * "build" type
     *
     * @return type instance
     */
    public T build() {
        return instance;
    }

    /**
     * Get reflective builder instance from type
     *
     * @param type class
     * @param <T>  type
     * @return reflective builder instance
     * @throws InvocationTargetException thrown while invoking instance
     * @throws NoSuchMethodException     thrown when an empty constructor cannot be found
     * @throws IllegalAccessException    thrown while accessing type
     * @throws InstantiationException    thrown while instancing type
     */
    public static <T> ReflectiveBuilder<T> of(Class<T> type) throws InvocationTargetException,
            NoSuchMethodException,
            IllegalAccessException,
            InstantiationException {
        return of(ReflectedType.of(type));
    }

    /**
     * Get reflective builder instance from type
     *
     * @param type reflective type instance
     * @param <T>  type
     * @return reflective builder instance
     * @throws InvocationTargetException thrown while invoking instance
     * @throws NoSuchMethodException     thrown when an empty constructor cannot be found
     * @throws IllegalAccessException    thrown while accessing type
     * @throws InstantiationException    thrown while instancing type
     */
    public static <T> ReflectiveBuilder<T> of(ReflectedType<T> type) throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            InstantiationException {
        T instance = type.getConstructor().newInstance();

        return ReflectiveBuilder.of(type, instance);
    }

    /**
     * Get reflective builder instance from type
     *
     * @param type     class
     * @param instance type instance
     * @param <T>      type
     * @return reflective builder instance
     */
    public static <T> ReflectiveBuilder<T> of(ReflectedType<T> type, T instance) {
        return new ReflectiveBuilder<>(type, instance);
    }

}
