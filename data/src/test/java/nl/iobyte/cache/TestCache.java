package nl.iobyte.cache;

import nl.iobyte.framework.data.cache.interfaces.ICache;

import java.util.concurrent.TimeUnit;

public class TestCache implements ICache {

    @Override
    public byte[] get(String key) {
        return new byte[0];
    }

    @Override
    public byte[] get(String table, String key) {
        return new byte[0];
    }

    @Override
    public void set(String key, byte[] value) {

    }

    @Override
    public void set(String table, String key, byte[] value) {

    }

    @Override
    public void timeout(String key, int timeout, TimeUnit unit) {

    }

    @Override
    public void timeout(String table, String key, int timeout, TimeUnit unit) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void remove(String table, String key) {

    }

    @Override
    public boolean has(String key) {
        return false;
    }

    @Override
    public boolean has(String table, String key) {
        return false;
    }

    @Override
    public void closeConnection() {

    }

    @Override
    public String getId() {
        return "test_cache";
    }

}
