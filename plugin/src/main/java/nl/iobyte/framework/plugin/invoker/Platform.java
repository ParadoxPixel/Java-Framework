package nl.iobyte.framework.plugin.invoker;

public enum Platform {

    STANDALONE,
    NODE,
    PROXY;

    /**
     * Check if platform is standalone
     *
     * @return whether platform is standalone
     */
    public boolean isStandalone() {
        return this == STANDALONE;
    }

    /**
     * Check if platform is a node
     *
     * @return whether platform is a node
     */
    public boolean isNode() {
        return this == NODE;
    }

    /**
     * Check if platform is proxy
     *
     * @return whether platform is a proxy
     */
    public boolean isProxy() {
        return this == PROXY;
    }

    /**
     * Check if platform is part of a network
     *
     * @return whether platform is part of a network
     */
    public boolean isNetwork() {
        return isNode() || isProxy();
    }

}
