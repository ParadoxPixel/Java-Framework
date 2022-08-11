package nl.iobyte.framework.rest.validator.interfaces;

public interface IValidationRule<T> {

    /**
     * Test object with predicate
     *
     * @param instance referencing object
     * @param obj      input of predicate
     * @return whether object passes predicate
     */
    boolean test(Object instance, T obj) throws Exception;

}
