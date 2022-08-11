package nl.iobyte.framework.generic.exceptional;

public interface ExceptionalFunction<T, R> {

    /**
     * Apply object to function
     *
     * @param obj input of function
     * @return output of function
     */
    R apply(T obj);

}
