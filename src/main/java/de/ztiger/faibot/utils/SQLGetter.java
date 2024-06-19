package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.entities.Member;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static de.ztiger.faibot.FaiBot.*;

@SuppressWarnings("ConstantConditions")
public class SQLGetter {

    public int getId(String id) {
        return (int) mariaDB.getValue("SELECT userid FROM users WHERE dcid = ?", id);
    }

    public String getMemberById(int id) {
        return (String) mariaDB.getValue("SELECT dcid FROM users WHERE userid = ?", id);
    }

    public int getLevel(String id) {
        return (int) mariaDB.getValue("SELECT level FROM stats WHERE userid = ?", getId(id));
    }

    public int getXP(String id) {
        return (int) mariaDB.getValue("SELECT xp FROM stats WHERE userid = ?", getId(id));
    }

    public int getPoints(String id) {
        return (int) mariaDB.getValue("SELECT points FROM stats WHERE userid = ?", getId(id));
    }

    public int getMessages(String id) {
        return (int) mariaDB.getValue("SELECT messages FROM stats WHERE userid = ?", getId(id));
    }

    public int getStreak(String id) {
        return (int) mariaDB.getValue("SELECT streak FROM stats WHERE userid = ?", getId(id));
    }

    public List<String> getInventory(String id) {
        List<String> items = new ArrayList<>();

        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT typ FROM backpacks WHERE userid = ?");
            ps.setInt(1, getId(id));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) items.add(rs.getString("typ"));

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return items;
    }

    public String getCardColor(String id) {
        return (String) mariaDB.getValue("SELECT cardColor FROM users WHERE dcid = ?", id);
    }

    public List<Member> getTopMembers(int offset) {
        List<Member> members = new ArrayList<>();

        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT * FROM stats ORDER BY xp DESC LIMIT ?, ?");
            ps.setInt(1, offset);
            ps.setInt(2, 10);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                members.add(getShardManager().getGuildById(config.get("GUILD")).getMemberById(getMemberById(rs.getInt("userid"))));

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return members;
    }

    public int getRank(String id) {
        return (int) mariaDB.getValue("SELECT FIND_IN_SET(xp, (SELECT GROUP_CONCAT(DISTINCT xp ORDER BY xp DESC) FROM stats)) AS 'rank', userid, xp FROM stats WHERE userid = ? ORDER BY xp DESC", getId(id));
    }

    public String getLastReward(String id) {
        return (String) mariaDB.getValue("SELECT lastReward FROM users WHERE dcid = ?", id);
    }

    public String getLastVideo() {
        return (String) mariaDB.getValue("SELECT content FROM cache WHERE typ = ?", "lastVideo");
    }

    public boolean userExists(String id) {
        return mariaDB.getValue("SELECT * FROM users WHERE dcid = ?", id) != null;
    }
}
