package de.ztiger.faibot.utils;

import de.ztiger.faibot.FaiBot;

import java.sql.*;

import static de.ztiger.faibot.FaiBot.logger;

public class MariaDB {

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
        if (!isConnected())
            connection = DriverManager.getConnection("jdbc:mariadb://" + url + "/" + database + "?user=" + username + "&password=" + password);
    }

    public Connection getConnection() {
        if (!isConnected()) {
            try {
                connect();
            } catch (ClassNotFoundException | SQLException e) {
                logger.error(e.getMessage());
            }
        }

        return connection;
    }

    protected Object getValue(String query, Object... params) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getObject(1);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    protected void setValue(String query, Object... params) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);

            ps.executeUpdate();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
