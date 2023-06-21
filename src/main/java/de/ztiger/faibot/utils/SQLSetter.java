package de.ztiger.faibot.utils;

import java.sql.PreparedStatement;

import static de.ztiger.faibot.FaiBot.getter;
import static de.ztiger.faibot.FaiBot.mysql;

public class SQLSetter {

    public void addUser(String id) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO users (dcid) VALUES (?)");
            ps.setString(1, id);
            ps.executeUpdate();

            ps = mysql.getConnection().prepareStatement("INSERT INTO stats (userid) VALUES (?)");
            ps.setInt(1, getter.getId(id));
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addXP(String id, int amount) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE stats SET xp = xp + ? WHERE userid = ?");
            ps.setInt(1, amount);
            ps.setInt(2, getter.getId(id));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPoints(String id, int amount) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE stats SET points = points + ? WHERE userid = ?");
            ps.setInt(1, amount);
            ps.setInt(2, getter.getId(id));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePoints(String id, int amount) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE stats SET points = points - ? WHERE userid = ?");
            ps.setInt(1, amount);
            ps.setInt(2, getter.getId(id));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String id) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE stats SET messages = messages + 1 WHERE userid = ?");
            ps.setInt(1, getter.getId(id));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLevel(String id) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE stats SET level = level + 1 WHERE userid = ?");
            ps.setInt(1, getter.getId(id));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addInventory(String id, String type) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO backpacks (userid, typ) VALUES (?, ?)");
            ps.setInt(1, getter.getId(id));
            ps.setString(2, type);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCardColor(String id, String color) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE users SET cardColor = ? WHERE dcid = ?");
            ps.setString(1, color);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLastReward(String id, String time) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("UPDATE users SET lastReward = ? WHERE dcid = ?");
            ps.setString(1, time);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLastVideo(String id) {
        try {
            PreparedStatement ps = mysql.getConnection().prepareStatement("INSERT INTO cache (content, typ) VALUES (?, ?) ON DUPLICATE KEY UPDATE content = ?");
            ps.setString(1, id);
            ps.setString(2, "lastVideo");
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
