package nl.iobyte.framework.generic.reflections.objects;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.generic.reflections.components.ReflectedConstructor;
import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.generic.reflections.components.ReflectedMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReflectedType<T> extends ReflectedAnnotation {

    private final Class<T> type;

    public ReflectedType(Class<T> type) {
        super(type);
        this.type = type;
    }

    /**
     * Get type
     *
     * @return type
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Get simple name of type
     *
     * @return name of type
     */
    public String getName() {
        return type.getSimpleName();
    }

    /**
     * Get name of type
     *
     * @return name of type
     */
    public String getFullName() {
        return type.getName();
    }

    /**
     * Get name of package
     *
     * @return name of package
     */
    public String getPackageName() {
        return type.getPackageName();
    }

    /**
     * Check if type is assignable from another type
     *
     * @param clazz type to check
     * @return is assignable
     */
    public boolean isAssignable(Class<?> clazz) {
        return type.isAssignableFrom(clazz);
    }

    /**
     * Check if type is assignable from an object
     *
     * @param obj object to check
     * @return is assignable
     */
    public boolean isAssignable(Object obj) {
        return type.isInstance(obj);
    }

    /**
     * Cast object to type
     *
     * @param obj to cast
     * @return object of type
     */
    public T cast(Object obj) {
        return type.cast(obj);
    }

    /**
     * Get all constructors in type
     *
     * @return list of reflected constructor instances
     */
    public List<ReflectedConstructor<T>> getConstructors() {
        //noinspection unchecked
        return Arrays.stream(type.getDeclaredConstructors())
                     .map(c -> (Constructor<T>) c)
                     .map(ReflectedConstructor::of)
                     .collect(Collectors.toList());
    }

    /**
     * Get reflected constructor of type by parameter types
     *
     * @param parameters array of parameter types
     * @return reflected constructor instance
     * @throws NoSuchMethodException thrown when no constructor found for parameters
     */
    public ReflectedConstructor<T> getConstructor(Class<?>... parameters) throws NoSuchMethodException {
        //noinspection unchecked
        return Arrays.stream(type.getDeclaredConstructors())
                     .map(c -> (Constructor<T>) c)
                     .map(ReflectedConstructor::of)
                     .filter(c -> c.checkParameters(parameters))
                     .findAny()
                     .orElseThrow();
    }

    /**
     * Get reflected constructor of type by parameters
     *
     * @param parameters array of parameters
     * @return reflected constructor instance
     * @throws NoSuchMethodException thrown when no constructor found for parameters
     */
    public ReflectedConstructor<T> getConstructor(Object... parameters) throws NoSuchMethodException {
        return getConstructor(
                Arrays.stream(parameters)
                      .map(Object::getClass)
                      .toArray(Class[]::new)
        );
    }

    /**
     * Get all fields in type
     *
     * @return list of reflected field instances
     */
    public List<ReflectedField<?>> getFields() {
        Class<?> clazz = this.type;
        List<ReflectedField<?>> fields = new ArrayList<>();
        while(clazz != null) {
            Arrays.stream(clazz.getDeclaredFields())
                  .map(field -> ReflectedField.of(field, field.getType()))
                  .forEach(fields::add);

            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    /**
     * Get field matching predicate and type
     *
     * @param predicate condition field should pass
     * @param type      of field
     * @param <R>       type
     * @return reflected field instance
     * @throws IllegalArgumentException thrown when failing to find field matching predicate and type
     */
    public <R> ReflectedField<R> getField(Predicate<ReflectedField<R>> predicate, Class<R> type) throws
            IllegalArgumentException {
        //noinspection unchecked
        type = (Class<R>) TypeConverter.normalise(type);

        Class<?> clazz = this.type;
        while(clazz != null) {
            for(Field field : clazz.getDeclaredFields())
                if(type == null || type.isAssignableFrom(field.getType()))
                    if(predicate.test(ReflectedField.of(field, type)))
                        return ReflectedField.of(field, type);

            clazz = clazz.getSuperclass();
        }

        throw new IllegalArgumentException(
                "unable to find field in type " +
                        getFullName() +
                        " that matches predicate" +
                        (type != null ? " and type " + type.getName() : "")
        );
    }

    /**
     * Get field by name
     *
     * @param name of field
     * @return reflected field instance
     */
    public ReflectedField<?> getFieldByName(String name) {
        return getField(field -> name.equals(field.getName()), null);
    }

    /**
     * Get field by type
     *
     * @param type of field
     * @param <R>  type
     * @return reflected field instance
     */
    public <R> ReflectedField<R> getFieldByType(Class<R> type) {
        return getField(field -> true, type);
    }

    /**
     * Get field by name and type
     *
     * @param name of field
     * @param type of field
     * @param <R>  type
     * @return reflected field instance of type
     */
    public <R> ReflectedField<R> getField(String name, Class<R> type) {
        return getField(field -> name.equals(field.getName()), type);
    }

    /**
     * Get all methods in type
     *
     * @return list of reflected method instances
     */
    public List<ReflectedMethod<?>> getMethods() {
        Class<?> clazz = this.type;
        List<ReflectedMethod<?>> methods = new ArrayList<>();
        while(clazz != null) {
            Arrays.stream(clazz.getDeclaredMethods())
                  .map(method -> ReflectedMethod.of(method, method.getReturnType()))
                  .forEach(methods::add);

            clazz = clazz.getSuperclass();
        }

        return methods;
    }

    /**
     * Get method matching predicate and return type
     *
     * @param predicate condition method should pass
     * @param type      of return value
     * @param <R>       type
     * @return reflected method instance
     * @throws IllegalArgumentException thrown when failing to find method matching predicate and type
     */
    public <R> ReflectedMethod<R> getMethod(Predicate<ReflectedMethod<R>> predicate, Class<R> type) throws
            IllegalArgumentException {
        //noinspection unchecked
        type = (Class<R>) TypeConverter.normalise(type);

        Class<?> clazz = this.type;
        while(clazz != null) {
            for(Method method : clazz.getDeclaredMethods()) {
                if(type == null || type.isAssignableFrom(TypeConverter.normalise(method.getReturnType())))
                    if(predicate.test(ReflectedMethod.of(method, type)))
                        return ReflectedMethod.of(method, type);
            }

            clazz = clazz.getSuperclass();
        }

        throw new IllegalArgumentException(
                "unable to find method in type " +
                        getFullName() +
                        " that matches predicate" +
                        (type != null ? " and return type " + type.getName() : "")
        );
    }

    /**
     * Get method by name
     *
     * @param name of method
     * @return reflected method instance
     */
    public ReflectedMethod<?> getMethodByName(String name) {
        return getMethod(method -> name.equals(method.getName()), null);
    }

    /**
     * Get method by return value type
     *
     * @param type of return value
     * @param <R>  type
     * @return reflected method instance
     */
    public <R> ReflectedMethod<R> getMethodByReturnType(Class<R> type) {
        return getMethod(method -> true, type);
    }

    /**
     * Get method by parameters
     *
     * @param parameters array of types
     * @return reflected method instance
     */
    public ReflectedMethod<Object> getMethodByParameters(Class<?>... parameters) {
        return getMethod(method -> method.checkParameters(parameters), Object.class);
    }

    /**
     * Get method by parameters and return type
     *
     * @param type       of return value
     * @param parameters array of types
     * @param <R>        type
     * @return reflected method instance
     */
    public <R> ReflectedMethod<R> getMethod(Class<R> type, Class<?>... parameters) {
        return getMethod(method -> method.checkParameters(parameters), type);
    }

    /**
     * Get reflected type instance of type
     *
     * @param type class
     * @param <T>  type
     * @return reflected type instance
     */
    public static <T> ReflectedType<T> of(Class<T> type) {
        return new ReflectedType<>(type);
    }

}
