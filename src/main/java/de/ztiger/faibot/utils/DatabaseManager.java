package de.ztiger.faibot.utils;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.ztiger.faibot.data.Placement;
import de.ztiger.faibot.data.Season;
import de.ztiger.faibot.data.TwitchUser;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    @Getter
    private ConnectionSource connectionSource;

    public DatabaseManager(Dotenv env) throws SQLException {
        String url = env.get("DB_URL");
        String username = env.get("DB_USER");
        String password = env.get("DB_PASSWORD");

        connectionSource = new JdbcConnectionSource(url, username, password);

        TableUtils.createTableIfNotExists(connectionSource, Placement.class);
        TableUtils.createTableIfNotExists(connectionSource, Season.class);
        TableUtils.createTableIfNotExists(connectionSource, TwitchUser.class);
    }

    public void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                logger.error("Error closing database connection: ", e);
            }
        }
    }
}
