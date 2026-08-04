package de.ztiger.faibot.db;

import static de.ztiger.faibot.FaiBot.*;

public class SQLSetter {

    public void setLastVideo(String id) {
        mariaDB.setValue("INSERT INTO cache (content, typ) VALUES (?, ?) ON DUPLICATE KEY UPDATE content = ?", id, "lastVideo", id);
    }
}
