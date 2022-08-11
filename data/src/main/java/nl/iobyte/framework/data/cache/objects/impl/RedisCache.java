package nl.iobyte.framework.data.cache.objects.impl;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.resource.ClientResources;
import nl.iobyte.framework.data.cache.objects.AbstractCache;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedisCache extends AbstractCache {

    private static final RedisCodec<String, byte[]> codec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE);
    private final RedisClient client;
    private final StatefulRedisConnection<String, byte[]> connection;

    public RedisCache(String id, RedisURI uri, ClientResources resources) {
        super(id);
        client = RedisClient.create(resources, uri);
        connection = client.connect(codec);
    }

    @Override
    public byte[] get(String key) throws Exception {
        return connection.sync().get(key);
    }

    @Override
    public byte[] get(String table, String key) throws Exception {
        return connection.sync().hget(table, key);
    }

    @Override
    public void set(String key, byte[] value) throws Exception {
        connection.async().set(key, value).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void set(String table, String key, byte[] value) throws Exception {
        connection.async().hset(table, key, value).exceptionally(ex -> {
            ex.printStackTrace();
            return false;
        });
    }

    @Override
    public void timeout(String key, int timeout, TimeUnit unit) {
        connection.async().expire(key, Duration.of(timeout, unit.toChronoUnit())).exceptionally(ex -> {
            ex.printStackTrace();
            return false;
        });
    }

    @Override
    public void timeout(String table, String key, int timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("redis does not support hash-map key expiry");
    }

    @Override
    public void remove(String key) {
        connection.sync().del(key);
    }

    @Override
    public void remove(String table, String key) {
        connection.sync().hdel(table, key);
    }

    @Override
    public boolean has(String key) {
        return connection.sync().exists(key) == 1;
    }

    @Override
    public boolean has(String table, String key) {
        return connection.sync().hexists(table, key);
    }

    @Override
    public void closeConnection() {
        connection.close();
        client.shutdown();
    }

}
