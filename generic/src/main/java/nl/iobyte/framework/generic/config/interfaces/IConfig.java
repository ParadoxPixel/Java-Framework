package nl.iobyte.framework.generic.config.interfaces;

import nl.iobyte.framework.generic.exceptional.ExceptionalSupplier;
import nl.iobyte.framework.generic.serializer.interfaces.ISerializer;
import nl.iobyte.framework.structures.omap.interfaces.IObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface IConfig extends IConfigSection, IObject<String> {

    /**
     * Get serializer used by config
     *
     * @return serializer instance
     */
    ISerializer getSerializer();

    /**
     * Get deserialized contents of config
     *
     * @return key -> value mapping
     */
    Map<String, Object> getContents();

    /**
     * Get config data supplier
     *
     * @return data supplier
     */
    ExceptionalSupplier<InputStream> getSupplier();

    /**
     * Load configuration
     */
    void load() throws Exception;

    /**
     * Save config to output stream
     *
     * @param stream output stream
     */
    void save(OutputStream stream) throws Exception;

}
