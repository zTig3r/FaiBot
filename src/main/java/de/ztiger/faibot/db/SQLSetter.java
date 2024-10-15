package de.ztiger.faibot.db;

import static de.ztiger.faibot.FaiBot.*;

public class SQLSetter {

    public void addUser(String id) {
        mariaDB.setValue("INSERT INTO users (dcid) VALUES (?)", id);
        mariaDB.setValue("INSERT INTO stats (userid) VALUES (?)", getter.getId(id));
    }

    public void addXP(String id, int amount) {
        mariaDB.setValue("UPDATE stats SET xp = xp + ? WHERE userid = ?", amount, getter.getId(id));
    }

    public void addPoints(String id, int amount) {
        mariaDB.setValue("UPDATE stats SET points = points + ? WHERE userid = ?", amount, getter.getId(id));
    }

    public void removePoints(String id, int amount) {
        mariaDB.setValue("UPDATE stats SET points = points - ? WHERE userid = ?", amount, getter.getId(id));
    }

    public void addMessage(String id) {
        mariaDB.setValue("UPDATE stats SET messages = messages + 1 WHERE userid = ?", getter.getId(id));
    }

    public void addLevel(String id) {
        mariaDB.setValue("UPDATE stats SET level = level + 1 WHERE userid = ?", getter.getId(id));
    }

    public void addInventory(String id, String type) {
        mariaDB.setValue("INSERT INTO backpacks (userid, typ) VALUES (?, ?)", getter.getId(id), type);
    }

    public void setCardColor(String id, String color) {
        mariaDB.setValue("UPDATE users SET cardColor = ? WHERE dcid = ?", color, id);
    }

    public void setLastReward(String id, String time) {
        mariaDB.setValue("UPDATE users SET lastReward = ? WHERE dcid = ?", time, id);
    }

    public void increaseStreak(String id) {
        mariaDB.setValue("UPDATE stats SET streak = streak + 1 WHERE userid = ?", getter.getId(id));
    }

    public void resetStreak(String id) {
        mariaDB.setValue("UPDATE stats SET streak = 1 WHERE userid = ?", getter.getId(id));
    }

    public void setLastVideo(String id) {
        mariaDB.setValue("INSERT INTO cache (content, typ) VALUES (?, ?) ON DUPLICATE KEY UPDATE content = ?", id, "lastVideo", id);
    }
}
