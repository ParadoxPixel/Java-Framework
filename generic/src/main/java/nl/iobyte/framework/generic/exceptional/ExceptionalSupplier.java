package nl.iobyte.framework.generic.exceptional;

public interface ExceptionalSupplier<T> {

    T get() throws Exception;

}
