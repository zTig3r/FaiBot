package de.ztiger.faibot.db;

import static de.ztiger.faibot.FaiBot.mariaDB;

public class TableCreator {

    public static void createTables() {
        mariaDB.createTable("CREATE TABLE IF NOT EXISTS users (userid INT(100) NOT NULL AUTO_INCREMENT, dcid VARCHAR(100), cardColor VARCHAR(100) DEFAULT '#94c6f3', lastReward VARCHAR(100) DEFAULT NULL, PRIMARY KEY (userid), UNIQUE (dcid))");
        mariaDB.createTable("CREATE TABLE IF NOT EXISTS stats (userid INT(100), xp INT(100) DEFAULT 0, level INT(100) DEFAULT 0, points INT(100) DEFAULT 0, messages INT(100) DEFAULT 0, streak INT(100) DEFAULT 0, PRIMARY KEY (userid), UNIQUE (userid))");
        mariaDB.createTable("CREATE TABLE IF NOT EXISTS backpacks (itemid INT(100) NOT NULL AUTO_INCREMENT, userid INT(100), typ VARCHAR(100), PRIMARY KEY (itemid))");
        mariaDB.createTable("CREATE TABLE IF NOT EXISTS cache (typ VARCHAR(100), content VARCHAR(100), PRIMARY KEY (typ))");
    }
}
