package de.ztiger.faibot.db;

import static de.ztiger.faibot.FaiBot.*;

public class SQLGetter {

    public String getLastVideo() {
        return (String) mariaDB.getValue("SELECT content FROM cache WHERE typ = ?", "lastVideo");
    }
}
