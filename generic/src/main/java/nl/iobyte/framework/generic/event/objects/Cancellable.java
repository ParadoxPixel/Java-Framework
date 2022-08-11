package nl.iobyte.framework.generic.event.objects;

import nl.iobyte.framework.generic.event.interfaces.ICancellable;

public class Cancellable implements ICancellable {

    private boolean b = false;

    /**
     * {@inheritDoc}
     *
     * @return Boolean
     */
    public boolean isCancelled() {
        return b;
    }

    /**
     * {@inheritDoc}
     */
    public void cancel() {
        b = true;
    }

}
