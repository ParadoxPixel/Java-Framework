package nl.iobyte.framework.generic.serializer.interfaces;

import java.io.InputStream;

public interface ISerializer {

    /**
     * Serialize object to byte array
     *
     * @param obj object instance
     * @return byte array
     */
    byte[] to(Object obj);

    /**
     * Deserialize byte array to object
     *
     * @param bytes byte array
     * @param type  of object
     * @param <T>   type of object
     * @return object instance
     */
    <T> T from(byte[] bytes, Class<T> type);

    /**
     * Deserialize input stream to object
     *
     * @param stream input stream
     * @param type   of object
     * @param <T>    type of object
     * @return object instance
     */
    <T> T from(InputStream stream, Class<T> type);

}
