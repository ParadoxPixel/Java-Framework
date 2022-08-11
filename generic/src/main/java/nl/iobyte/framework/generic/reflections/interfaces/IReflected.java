package nl.iobyte.framework.generic.reflections.interfaces;

public interface IReflected {

    /**
     * Get name of reflected element
     *
     * @return name
     */
    String getName();

    /**
     * Get "snake_case" of name
     *
     * @return snake cased name
     */
    String getSnakeCase();

}
