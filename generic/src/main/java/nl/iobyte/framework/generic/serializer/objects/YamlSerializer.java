package nl.iobyte.framework.generic.serializer.objects;

import nl.iobyte.framework.generic.serializer.interfaces.ISerializer;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class YamlSerializer implements ISerializer {

    private final Yaml yaml = new Yaml();

    /**
     * {@inheritDoc}
     *
     * @param obj Object
     * @return Byte[]
     */
    public byte[] to(Object obj) {
        return yaml.dumpAsMap(obj).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     *
     * @param bytes Byte[]
     * @param type  Class<T>
     * @param <T>   T
     * @return T
     */
    public <T> T from(byte[] bytes, Class<T> type) {
        return from(new ByteArrayInputStream(bytes), type);
    }

    /**
     * {@inheritDoc}
     *
     * @param stream InputStream
     * @param type   Class<T>
     * @param <T>    T
     * @return T
     */
    public <T> T from(InputStream stream, Class<T> type) {
        try {
            try(stream) {
                return yaml.loadAs(
                        new InputStreamReader(stream),
                        type
                );
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
