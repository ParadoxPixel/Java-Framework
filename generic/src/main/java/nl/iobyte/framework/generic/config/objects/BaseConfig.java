package nl.iobyte.framework.generic.config.objects;

import nl.iobyte.framework.generic.config.interfaces.IConfig;
import nl.iobyte.framework.generic.exceptional.ExceptionalSupplier;
import nl.iobyte.framework.generic.serializer.interfaces.ISerializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.TreeMap;

public class BaseConfig extends BaseConfigSection implements IConfig {

    private final String id;
    private final ISerializer serializer;
    private final ExceptionalSupplier<InputStream> supplier;

    public BaseConfig(String id, ISerializer serializer, ExceptionalSupplier<InputStream> supplier) {
        this.id = id;
        this.serializer = serializer;
        this.supplier = supplier;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ISerializer getSerializer() {
        return serializer;
    }

    @Override
    public ExceptionalSupplier<InputStream> getSupplier() {
        return supplier;
    }

    @Override
    public void load() throws Exception {
        try(InputStream stream = supplier.get()) {
            if(stream == null || stream.available() == 0)
                throw new IllegalStateException("supplier either returned null or stream is unavailable");

            //noinspection unchecked
            TreeMap<String, Object> contents = serializer.from(stream, TreeMap.class);
            if(contents == null)
                return;

            setContents(contents);
        }
    }

    @Override
    public void save(OutputStream stream) throws Exception {
        byte[] bytes = serializer.to(getContents());
        stream.write(bytes);
    }

}
