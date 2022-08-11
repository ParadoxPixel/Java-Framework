package nl.iobyte.framework.generic.event.interfaces;

public interface ICancellable {

    /**
     * Check if event was cancelled
     *
     * @return Boolean
     */
    boolean isCancelled();

    /**
     * Cancel event
     */
    void cancel();

}
