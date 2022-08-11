package nl.iobyte.framework.generic.config.interfaces;

import java.util.Map;
import java.util.Set;

public interface IConfigSection {

    /**
     * Get object at path
     *
     * @param path to object
     * @return object
     */
    Object get(String path);

    /**
     * Get object at path as type
     *
     * @param path to object
     * @param type of object
     * @param <T>  type of object
     * @return object
     */
    <T> T getAs(String path, Class<T> type);

    /**
     * Set object at path
     *
     * @param path  to object
     * @param value to set path to
     */
    void set(String path, Object value);

    /**
     * Get byte at path
     *
     * @param path to byte
     * @return byte
     */
    byte getByte(String path);

    /**
     * Get character at path
     *
     * @param path to character
     * @return character
     */
    char getCharacter(String path);

    /**
     * Get string at path
     *
     * @param path to string
     * @return string
     */
    String getString(String path);

    /**
     * Get short at path
     *
     * @param path to short
     * @return short
     */
    short getShort(String path);

    /**
     * Get boolean at path
     *
     * @param path to boolean
     * @return boolean
     */
    boolean getBoolean(String path);

    /**
     * Get integer at path
     *
     * @param path to integer
     * @return integer
     */
    int getInteger(String path);

    /**
     * Get long at path
     *
     * @param path to long
     * @return long
     */
    long getLong(String path);

    /**
     * Get double at path
     *
     * @param path to double
     * @return double
     */
    double getDouble(String path);

    /**
     * Get float at path
     *
     * @param path to float
     * @return float
     */
    float getFloat(String path);

    /**
     * Get keys at path
     *
     * @param path to keys
     * @return key set
     */
    Set<String> getKeys(String path);

    /**
     * Get config section at path
     *
     * @param path to section
     * @return config section instance
     */
    IConfigSection getSection(String path);

    /**
     * Get deserialized contents of config
     *
     * @return key -> value mapping
     */
    Map<String, Object> getContents();

}
