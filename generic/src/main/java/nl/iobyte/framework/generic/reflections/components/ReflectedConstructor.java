package nl.iobyte.framework.generic.reflections.components;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.generic.reflections.objects.ReflectedAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ReflectedConstructor<T> extends ReflectedAnnotation {

    private final Constructor<T> constructor;
    private final Class<?>[] parameterTypes;

    public ReflectedConstructor(Constructor<T> constructor) {
        super(constructor);
        constructor.trySetAccessible();

        this.constructor = constructor;
        this.parameterTypes = Arrays.stream(constructor.getParameterTypes())
                                    .map(TypeConverter::normalise)
                                    .toArray(Class[]::new);
    }

    /**
     * Get constructor instance
     *
     * @return constructor instance
     */
    public Constructor<T> getConstructor() {
        return constructor;
    }

    /**
     * Get type of constructor
     *
     * @return type of constructor
     */
    public Class<T> getType() {
        return constructor.getDeclaringClass();
    }

    /**
     * Check if constructor requires parameters
     *
     * @return needs parameters
     */
    public boolean hasParameters() {
        return getParameterCount() > 0;
    }

    /**
     * Get amount of parameters constructor needs
     *
     * @return amount of parameters
     */
    public int getParameterCount() {
        return constructor.getParameterCount();
    }

    /**
     * Check if parameters are correct
     *
     * @param parameters array of objects
     * @return valid parameters
     */
    public boolean checkParameters(Object... parameters) {
        return checkParameters(
                Arrays.stream(parameters)
                      .map(Object::getClass)
                      .toArray(Class[]::new)
        );
    }

    public boolean checkParameters(Class<?>... parameters) {
        if(getParameterCount() != parameters.length) return false;

        for(int i = 0; i < getParameterCount(); i++)
            if(!parameterTypes[i].isAssignableFrom(TypeConverter.normalise(parameters[i]))) return false;

        return true;
    }

    /**
     * Get new instance of type with parameters
     *
     * @param parameters array of parameters
     * @return type instance
     * @throws IllegalArgumentException    thrown while checking arguments
     * @throws IllegalAccessException      thrown while accessing constructor
     * @throws InstantiationException      thrown while instancing type
     * @throws InvocationTargetException   thrown while invoking instance
     * @throws ExceptionInInitializerError thrown while initializing instance
     */
    public T newInstance(Object... parameters) throws
            IllegalAccessException,
            IllegalArgumentException,
            InstantiationException,
            InvocationTargetException,
            ExceptionInInitializerError {
        if(!checkParameters(parameters))
            throw new IllegalArgumentException(
                    "invalid parameters, expected: " +
                            Arrays.toString(
                                    Arrays.stream(constructor.getParameterTypes())
                                          .map(Class::getName)
                                          .toArray(String[]::new)
                            ) +
                            " got: " +
                            Arrays.toString(
                                    Arrays.stream(parameters)
                                          .map(p -> p.getClass().getName())
                                          .toArray(String[]::new)
                            )
            );

        return constructor.newInstance(parameters);
    }

    /**
     * Cast constructor return value to type
     *
     * @param type to cast to
     * @param <R>  type of constructor return value
     * @return reflected constructor instance
     */
    public <R> ReflectedConstructor<R> castTo(Class<R> type) {
        if(!type.isAssignableFrom(constructor.getDeclaringClass()))
            throw new IllegalArgumentException(
                    type.getName() +
                            " cannot be assigned from " +
                            constructor.getDeclaringClass().getName()
            );

        //noinspection unchecked
        return ReflectedConstructor.of((Constructor<R>) constructor);
    }

    /**
     * Get reflected constructor instance from constructor
     *
     * @param constructor instance of constructor
     * @param <R>         return type of constructor
     * @return reflected constructor instance
     */
    public static <R> ReflectedConstructor<R> of(Constructor<R> constructor) {
        return new ReflectedConstructor<>(constructor);
    }

}
