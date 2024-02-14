package de.ztiger.faibot.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.FaiBot.mariaDB;

public class TableCreator {

    public static void createTables() {
        createTu();
        createTxp();
        createTb();
        createTc();
    }

    public static void createTu() {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS users "
                    + "(userid INT(100) NOT NULL AUTO_INCREMENT, dcid VARCHAR(100), cardColor VARCHAR(100) DEFAULT '#94c6f3', lastReward VARCHAR(100) DEFAULT NULL, PRIMARY KEY (userid), UNIQUE (dcid))");
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void createTxp() {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS stats "
                    + "(userid INT(100), xp INT(100) DEFAULT 0, level INT(100) DEFAULT 0, points INT(100) DEFAULT 0, messages INT(100) DEFAULT 0, PRIMARY KEY (userid), UNIQUE (userid))");
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void createTb() {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS backpacks "
                    + "(itemid INT(100) NOT NULL AUTO_INCREMENT, userid INT(100), typ VARCHAR(100), PRIMARY KEY (itemid))");
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static void createTc() {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS cache "
                    + "(typ VARCHAR(100), content VARCHAR(100), PRIMARY KEY (typ))");
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
}
