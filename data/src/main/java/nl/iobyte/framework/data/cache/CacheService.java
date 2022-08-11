package nl.iobyte.framework.data.cache;

import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import nl.iobyte.framework.data.cache.interfaces.ICache;
import nl.iobyte.framework.data.cache.objects.impl.KeyDBCache;
import nl.iobyte.framework.data.cache.objects.impl.RedisCache;
import nl.iobyte.framework.generic.config.ConfigService;
import nl.iobyte.framework.generic.config.interfaces.IConfig;
import nl.iobyte.framework.generic.service.annotations.Inject;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.structures.omap.ObjectMap;

public class CacheService extends ObjectMap<String, ICache> implements Service {

    @Inject
    private ConfigService configService;

    /**
     * Load all databases from config
     */
    public void start() {
        IConfig config = configService.get("caches");
        if(config == null)
            throw new IllegalStateException("unable to find configuration with id \"caches\"");

        for(String id : config.getKeys("caches")) {
            ICache cache;

            RedisURI uri;
            ClientResources resources;
            switch(config.getString("caches." + id + ".type")) {
                case "redis":
                    uri = RedisURI.builder()
                                  .withHost(config.getString("caches." + id + ".host"))
                                  .withPort(config.getInteger("caches." + id + ".port"))
                                  .withAuthentication(
                                          config.getString("caches." + id + ".username"),
                                          config.getString("caches." + id + ".password")
                                  ).build();

                    resources = ClientResources.builder()
                                               .ioThreadPoolSize(config.getInteger("caches." + id + ".threads"))
                                               .computationThreadPoolSize(config.getInteger("caches." + id + ".threads"))
                                               .build();

                    cache = new RedisCache(id, uri, resources);
                    break;
                case "keydb":
                    uri = RedisURI.builder()
                                  .withHost(config.getString("caches." + id + ".host"))
                                  .withPort(config.getInteger("caches." + id + ".port"))
                                  .withAuthentication(
                                          config.getString("caches." + id + ".username"),
                                          config.getString("caches." + id + ".password")
                                  ).build();

                    resources = ClientResources.builder()
                                               .ioThreadPoolSize(config.getInteger("caches." + id + ".threads"))
                                               .computationThreadPoolSize(config.getInteger("caches." + id + ".threads"))
                                               .build();

                    cache = new KeyDBCache(id, uri, resources);
                    break;
                default:
                    continue;
            }

            put(cache);
        }
    }

    @Override
    public void stop() {
        values().forEach(ICache::closeConnection);
    }

}
