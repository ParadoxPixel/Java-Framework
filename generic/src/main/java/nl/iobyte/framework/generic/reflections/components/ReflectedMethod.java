package nl.iobyte.framework.generic.reflections.components;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.generic.reflections.objects.ReflectedAnnotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectedMethod<T> extends ReflectedAnnotation {

    private final Method method;
    private final Class<T> type;
    private final Class<?>[] parameterTypes;

    public ReflectedMethod(Method method, Class<T> type) {
        super(type);
        method.trySetAccessible();

        this.method = method;
        this.type = type;
        this.parameterTypes = Arrays.stream(method.getParameterTypes())
                                    .map(TypeConverter::normalise)
                                    .toArray(Class[]::new);
    }

    /**
     * Get method instance
     *
     * @return method instance
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Get type of method return value
     *
     * @return type of method return value
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Get name of method
     *
     * @return name of method
     */
    public String getName() {
        return method.getName();
    }

    /**
     * Check if constructor requires parameters
     *
     * @return needs parameters
     */
    public boolean hasParameters() {
        return method.getParameterCount() == 0;
    }

    /**
     * Get methods parameter types
     *
     * @return type array
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
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

    /**
     * Check if parameters are correct
     *
     * @param parameters array of types
     * @return valid parameters
     */
    public boolean checkParameters(Class<?>... parameters) {
        if(method.getParameterCount() != parameters.length)
            return false;

        for(int i = 0; i < parameters.length; i++)
            if(!parameterTypes[i].isAssignableFrom(TypeConverter.normalise(parameters[i])))
                return false;

        return true;
    }

    /**
     * Invoke method with parameters and return raw value
     *
     * @param obj        instance to invoke method on
     * @param parameters array of parameters
     * @return raw method return value
     * @throws IllegalAccessException    thrown while accessing method
     * @throws InvocationTargetException thrown while invoking method
     */
    public Object invokeRaw(Object obj, Object... parameters) throws IllegalAccessException, InvocationTargetException {
        if(!method.getDeclaringClass().isInstance(obj))
            throw new IllegalArgumentException(
                    "invalid object type, expected: "
                            + method.getDeclaringClass().getName()
                            + " got: "
                            + obj.getClass().getName()
            );

        if(!checkParameters(parameters))
            throw new IllegalArgumentException(
                    "invalid parameters, expected: " +
                            Arrays.toString(
                                    Arrays.stream(method.getParameterTypes()).map(Class::getName)
                                          .toArray(String[]::new)
                            ) +
                            " got: " +
                            Arrays.toString(
                                    Arrays.stream(parameters).map(p -> p.getClass().getName())
                                          .toArray(String[]::new)
                            )
            );

        return method.invoke(obj, parameters);
    }

    /**
     * Invoke method with parameters and return casted value
     *
     * @param obj        instance to invoke method on
     * @param parameters array of parameters
     * @return casted method return value
     * @throws IllegalAccessException    thrown while accessing method
     * @throws InvocationTargetException thrown while invoking method
     * @throws ClassCastException        thrown while casting return value
     */
    public T invoke(Object obj, Object... parameters) throws
            IllegalAccessException,
            InvocationTargetException,
            ClassCastException {
        return type.cast(invokeRaw(obj, parameters));
    }

    /**
     * Cast method return value to type
     *
     * @param type to cast to
     * @param <R>  type of method return value
     * @return reflected method instance
     */
    public <R> ReflectedMethod<R> castTo(Class<R> type) {
        if(!type.isAssignableFrom(method.getReturnType()))
            throw new IllegalArgumentException(
                    type.getName()
                            + " cannot be assigned from "
                            + method.getReturnType().getName()
            );

        return ReflectedMethod.of(method, type);
    }

    /**
     * Get reflected method instance from method and type
     *
     * @param method instance of method
     * @param type   of method return value
     * @return reflected method instance
     */
    public static <T> ReflectedMethod<T> of(Method method, Class<T> type) {
        return new ReflectedMethod<>(method, type);
    }

}
