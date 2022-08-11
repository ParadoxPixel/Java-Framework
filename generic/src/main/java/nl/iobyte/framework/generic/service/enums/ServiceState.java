package nl.iobyte.framework.generic.service.enums;

public enum ServiceState {

    NONE(false, false),
    INIT(true, false),

    PRE_START(true, false),
    START(true, true),
    POST_START(true, true),

    PRE_STOP(false, false),
    STOP(false, false),
    POST_STOP(false, false);

    private final boolean init, start;

    ServiceState(boolean init, boolean start) {
        this.init = init;
        this.start = start;
    }

    /**
     * Check if service can initialise with state
     *
     * @return boolean
     */
    public boolean canInit() {
        return init;
    }

    /**
     * Check if service can start with state
     *
     * @return boolean
     */
    public boolean canStart() {
        return start;
    }

}
