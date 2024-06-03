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
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT userid FROM users WHERE dcid = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("userid");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public String getMemberById(int id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT dcid FROM users WHERE userid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("dcid");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public int getLevel(String id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT level FROM stats WHERE userid = ?");
            ps.setInt(1, getId(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("level");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public int getXP(String id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT xp FROM stats WHERE userid = ?");
            ps.setInt(1, getId(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("xp");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public int getPoints(String id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT points FROM stats WHERE userid = ?");
            ps.setInt(1, getId(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("points");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public int getMessages(String id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT messages FROM stats WHERE userid = ?");
            ps.setInt(1, getId(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("messages");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public int getStreak(String id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT streak FROM stats WHERE userid = ?");
            ps.setInt(1, getId(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("streak");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public List<String> getInventory(String id) {
        List<String> items = new ArrayList<>();

        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT typ FROM backpacks WHERE userid = ?");
            ps.setInt(1, getId(id));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(rs.getString("typ"));
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return items;
    }

    public String getCardColor(String id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT cardColor FROM users WHERE dcid = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("cardColor");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "null";
    }

    public ArrayList<Member> getTopMembers(int offset) {
        ArrayList<Member> members = new ArrayList<>();

        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT * FROM stats ORDER BY xp DESC LIMIT ?, ?");
            ps.setInt(1, offset);
            ps.setInt(2, 10);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                members.add(getShardManager().getGuildById(config.get("GUILD")).getMemberById(getMemberById(rs.getInt("userid"))));
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return members;
    }

    public int getRank(String id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT userid, xp, FIND_IN_SET(xp, (SELECT GROUP_CONCAT(DISTINCT xp ORDER BY xp DESC) FROM stats)) AS 'rank' FROM stats WHERE userid = ? ORDER BY xp DESC");

            ps.setInt(1, getId(id));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("rank");

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public String getLastReward(String id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT lastReward FROM users WHERE dcid = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("lastReward");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "null";
    }

    public String getLastVideo() {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT * FROM cache WHERE typ = 'lastVideo'");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getString("content");

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public boolean userExists(String id) {
        try {
            PreparedStatement ps = mariaDB.getConnection().prepareStatement("SELECT * FROM users WHERE dcid = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
