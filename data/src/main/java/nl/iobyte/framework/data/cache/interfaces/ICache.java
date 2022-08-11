package nl.iobyte.framework.data.cache.interfaces;

import nl.iobyte.framework.structures.omap.interfaces.IObject;

import java.util.concurrent.TimeUnit;

public interface ICache extends IObject<String> {

    /**
     * Get value of key
     *
     * @param key entry identifier
     * @return byte array value
     */
    byte[] get(String key) throws Exception;

    /**
     * Get value of key in table
     *
     * @param table name of table
     * @param key   entry identifier
     * @return byte array value
     */
    byte[] get(String table, String key) throws Exception;

    /**
     * Set key to value
     *
     * @param key   entry identifier
     * @param value byte array value
     */
    void set(String key, byte[] value) throws Exception;

    /**
     * Set key to value and expire after time
     *
     * @param key     entry identifier
     * @param value   byte array value
     * @param timeout amount of time
     * @param unit    type of time
     */
    default void set(String key, byte[] value, int timeout, TimeUnit unit) throws Exception {
        set(key, value);
        timeout(key, timeout, unit);
    }

    /**
     * Set key in table to value
     *
     * @param table name of table
     * @param key   entry identifier
     * @param value byte array value
     */
    void set(String table, String key, byte[] value) throws Exception;

    /**
     * Set key in table to value and expire after time
     *
     * @param table   name of table
     * @param key     entry identifier
     * @param value   byte array value
     * @param timeout amount of time
     * @param unit    type of time
     */
    default void set(String table, String key, byte[] value, int timeout, TimeUnit unit) throws Exception {
        set(table, key, value);
        timeout(table, key, timeout, unit);
    }

    /**
     * Set key to expire after time
     *
     * @param key     entry identifier
     * @param timeout amount of time
     * @param unit    type of time
     */
    void timeout(String key, int timeout, TimeUnit unit) throws Exception;

    /**
     * Set key to expire after time
     *
     * @param table   name of table
     * @param key     entry identifier
     * @param timeout amount of time
     * @param unit    type of time
     */
    void timeout(String table, String key, int timeout, TimeUnit unit) throws Exception;

    /**
     * Remove key
     *
     * @param key entry identifier
     */
    void remove(String key) throws Exception;

    /**
     * Remove key from table
     *
     * @param table name of table
     * @param key   entry identifier
     */
    void remove(String table, String key) throws Exception;

    /**
     * Check if cache has key
     *
     * @param key entry identifier
     * @return if entry exists
     */
    boolean has(String key) throws Exception;

    /**
     * Check if cache has key
     *
     * @param table table identifier
     * @param key   entry identifier
     * @return if entry exists
     */
    boolean has(String table, String key) throws Exception;

    /**
     * Close connection(s) to cache
     */
    void closeConnection();

}
