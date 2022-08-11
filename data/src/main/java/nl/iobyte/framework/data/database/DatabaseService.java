package nl.iobyte.framework.data.database;

import nl.iobyte.framework.data.database.interfaces.IDatabase;
import nl.iobyte.framework.data.database.objects.impl.MySQLDatabase;
import nl.iobyte.framework.data.database.objects.impl.SQLiteDatabase;
import nl.iobyte.framework.generic.config.ConfigService;
import nl.iobyte.framework.generic.config.interfaces.IConfig;
import nl.iobyte.framework.generic.service.annotations.Inject;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.structures.omap.ObjectMap;

import java.io.File;

public class DatabaseService extends ObjectMap<String, IDatabase> implements Service {

    @Inject
    private ConfigService configService;

    /**
     * Load all databases from config
     */
    @Override
    public void start() {
        IConfig config = configService.get("databases");
        if(config == null)
            throw new IllegalStateException("unable to find configuration with id \"databases\"");

        for(String id : config.getKeys("databases")) {
            IDatabase database;
            switch(config.getString("databases." + id + ".type")) {
                case "sqlite":
                    database = new SQLiteDatabase(id, new File(
                            config.getString("databases." + id + ".path")
                    ));
                    break;
                case "mysql":
                    database = new MySQLDatabase(
                            id,
                            config.getString("databases." + id + ".url"),
                            config.getString("databases." + id + ".host"),
                            config.getInteger("databases." + id + ".port"),
                            config.getString("databases." + id + ".database"),
                            config.getString("databases." + id + ".username"),
                            config.getString("databases." + id + ".password"),
                            config.getInteger("databases." + id + ".poolSize")
                    );
                    break;
                default:
                    continue;
            }

            put(database);
        }
    }

    @Override
    public void stop() {
        values().forEach(IDatabase::closeConnection);
    }

}
