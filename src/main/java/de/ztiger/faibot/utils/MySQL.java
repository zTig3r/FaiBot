package de.ztiger.faibot.utils;

import de.ztiger.faibot.FaiBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private final String url = FaiBot.config.get("DB_URL");
    private final String username = FaiBot.config.get("DB_USER");
    private final String password = FaiBot.config.get("DB_PASSWORD");
    private final String database = FaiBot.config.get("DB_NAME");

    private Connection connection;

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + url + "/" + database, username, password);
        }
    }

    public Connection getConnection() {
        if(!isConnected()) {
            try {
                connect();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }
}
