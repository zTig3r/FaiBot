package de.ztiger.faibot.db;

import static de.ztiger.faibot.FaiBot.mariaDB;

public class TableCreator {

    public static void createTables() {
          mariaDB.createTable("CREATE TABLE IF NOT EXISTS cache (typ VARCHAR(100), content VARCHAR(100), PRIMARY KEY (typ))");
    }
}
