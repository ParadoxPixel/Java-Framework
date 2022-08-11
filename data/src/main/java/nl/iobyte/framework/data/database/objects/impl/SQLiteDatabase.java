package nl.iobyte.framework.data.database.objects.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.iobyte.framework.data.database.objects.AbstractDatabase;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class SQLiteDatabase extends AbstractDatabase {

    private HikariDataSource source;

    public SQLiteDatabase(String id, File databaseFile) {
        super(id);
        String databaseName = databaseFile.getName();

        if(!databaseFile.exists()) {
            try {
                if(!databaseFile.createNewFile())
                    throw new IllegalStateException("Couldn't generate the database file \"" + databaseName + "\"");
            } catch(IOException e) {
                e.printStackTrace();
                return;
            }
        }

        HikariConfig config = new HikariConfig();
        config.setUsername(null);
        config.setPassword(null);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setConnectionTestQuery("SELECT 1");
        config.setMaximumPoolSize(5);

        Properties prop = new Properties();
        prop.setProperty("date_string_format", "yyyy-MM-dd HH:mm:ss");

        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setDataSourceProperties(prop);
        source = new HikariDataSource(config);
    }

    /**
     * Get Database Connection
     *
     * @return Connection
     */
    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    /**
     * Close Connection
     */
    public void closeConnection() {
        source.close();
    }

}
