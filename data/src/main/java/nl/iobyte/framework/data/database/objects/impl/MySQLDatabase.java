package nl.iobyte.framework.data.database.objects.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.iobyte.framework.data.database.objects.AbstractDatabase;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLDatabase extends AbstractDatabase {

    private final HikariDataSource source;

    public MySQLDatabase(
            String id,
            String url,
            String host,
            int port,
            String dbName,
            String username,
            String password,
            int poolSize
    ) {
        super(id);
        if(url == null || url.isEmpty())
            url = "jdbc:mysql://%host%:%port%/%database%?useSSL=false";

        url = url.replace("%host%", host);
        url = url.replace("%port%", String.valueOf(port));
        url = url.replace("%database%", dbName);

        HikariConfig config = new HikariConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(poolSize);
        config.setJdbcUrl(url);
        source = new HikariDataSource(config);
    }

    /**
     * Get Connection
     *
     * @return Connection
     */
    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    /**
     * Close connection
     */
    public void closeConnection() {
        source.close();
    }

}
