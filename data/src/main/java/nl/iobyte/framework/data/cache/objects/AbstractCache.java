package nl.iobyte.framework.data.cache.objects;

import nl.iobyte.framework.data.cache.interfaces.ICache;

public abstract class AbstractCache implements ICache {

    private final String id;

    public AbstractCache(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

}
