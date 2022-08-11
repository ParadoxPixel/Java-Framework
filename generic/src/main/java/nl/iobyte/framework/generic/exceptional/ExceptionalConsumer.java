package nl.iobyte.framework.generic.exceptional;

public interface ExceptionalConsumer<T> {

    void accept(T obj) throws Exception;

}
