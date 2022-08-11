package nl.iobyte.framework.generic.exceptional;

public interface ExceptionalPredicate<T> {

    /**
     * Test object with predicate
     *
     * @param obj input of predicate
     * @return whether object passes predicate
     */
    boolean test(T obj) throws Exception;

}
