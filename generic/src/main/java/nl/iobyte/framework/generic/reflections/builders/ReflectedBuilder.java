package nl.iobyte.framework.generic.reflections.builders;

import nl.iobyte.framework.generic.reflections.components.ReflectedConstructor;
import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("UnusedReturnValue")
public class ReflectedBuilder<T> {

    private final ReflectedType<T> type;
    private final ReflectedConstructor<T> constructor;
    private final Map<String, ReflectedField<?>> fieldMapping;

    public ReflectedBuilder(ReflectedType<T> type, ReflectedConstructor<T> constructor) {
        this.type = type;
        this.constructor = constructor;
        this.fieldMapping = new ConcurrentHashMap<>();
    }

    /**
     * Get type used in builder
     *
     * @return reflected type
     */
    public ReflectedType<T> getType() {
        return type;
    }

    /**
     * Add required field to builder
     *
     * @param name of field
     * @return same instance
     */
    public ReflectedBuilder<T> addField(String name) {
        fieldMapping.put(
                name,
                type.getFieldByName(name)
        );
        return this;
    }

    /**
     * Set field value for object
     *
     * @param obj   to apply value to
     * @param name  of field
     * @param value of field
     * @return same instance
     * @throws IllegalAccessException   thrown while setting field value
     * @throws IllegalArgumentException thrown when object type or value type is invalid
     */
    public ReflectedBuilder<T> setField(T obj, String name, Object value) throws
            IllegalAccessException,
            IllegalArgumentException {
        ReflectedField<?> field = fieldMapping.get(name);
        if(field == null)
            throw new IllegalArgumentException("no field known in builder with name \"" + name + "\"");

        field.setRawValue(obj, value);
        return this;
    }

    /**
     * Set field value for object and ignore exceptions
     *
     * @param obj   to apply value to
     * @param name  of field
     * @param value of field
     * @return same instance
     */
    public ReflectedBuilder<T> setFieldIgnore(T obj, String name, Object value) {
        try {
            setField(obj, name, value);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Build instance of object with values
     *
     * @param values field name -> value
     * @return instance of type
     * @throws InvocationTargetException thrown while invoking instance
     * @throws IllegalAccessException    thrown while accessing constructor/field
     * @throws InstantiationException    thrown while instancing type
     */
    public T build(Map<String, Object> values) throws InvocationTargetException,
            IllegalAccessException,
            InstantiationException {
        T obj = constructor.newInstance();
        for(Map.Entry<String, Object> entry : values.entrySet())
            setField(obj, entry.getKey(), entry.getValue());

        return obj;
    }

    /**
     * Build instance of object with values and ignore exceptions while setting fields
     *
     * @param values field name -> value
     * @return instance of type
     */
    public T buildIgnore(Map<String, Object> values) {
        T obj;
        try {
            obj = constructor.newInstance();
        } catch(Exception e) {
            throw new RuntimeException("something went wrong while tryin to acquire new object instance");
        }

        for(Map.Entry<String, Object> entry : values.entrySet())
            setFieldIgnore(obj, entry.getKey(), entry.getValue());

        return obj;
    }

    /**
     * Get new reflected builder instance from type
     *
     * @param type type
     * @param <T>  type
     * @return reflected builder instance
     * @throws NoSuchMethodException thrown when no empty constructor can be found
     */
    public static <T> ReflectedBuilder<T> of(Class<T> type) throws NoSuchMethodException {
        return of(ReflectedType.of(type));
    }

    /**
     * Get new reflected builder instance from type
     *
     * @param type type
     * @param <T>  type
     * @return reflected builder instance
     * @throws NoSuchMethodException thrown when no empty constructor can be found
     */
    public static <T> ReflectedBuilder<T> of(ReflectedType<T> type) throws NoSuchMethodException {
        return new ReflectedBuilder<>(
                type,
                type.getConstructor()
        );
    }

}
