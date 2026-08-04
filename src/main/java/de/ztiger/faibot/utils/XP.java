package de.ztiger.faibot.utils;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Xp;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static de.ztiger.faibot.FaiBot.*;

public class XP {

    private static final HashMap<Member, Long> userTimer = new HashMap<>();

    public static int getLastLevelsXP(int level) {
        return IntStream.rangeClosed(0, level).map(XP::calcXP).sum();
    }

    public static int calcXP(int level) {
        return 5 * (level * level) + (50 * level) + 100;
    }

    public static void checkLevelUp(Member member) {
        int level = getter.getLevel(member.getId());
        int xpForNextLevel = calcXP(level);
        int xp = getter.getXP(member.getId()) - getLastLevelsXP(level - 1);

        if (xp >= xpForNextLevel) {
            setter.addLevel(member.getId());
            level++;

            logger.info("{} reached level {}!", member.getEffectiveName(), level);
            ChannelProvider.sendEmbed(BotChannel.BOT, levelUpEmbed(member.getAsMention(), level, Localization.get(Xp.LEVEL_UP), member.getUser().getAvatarUrl()));
        }
    }

    public static boolean canGetXp(Member member) {
        if (userTimer.containsKey(member) && (((userTimer.get(member) / 1000) + 60) - (System.currentTimeMillis() / 1000)) <= 0) {
            userTimer.remove(member);
            return true;
        }

        return false;
    }

    public static void addUserTimer(Member member) {
        userTimer.put(member, System.currentTimeMillis());
    }

    private static MessageEmbed levelUpEmbed(String user, int level, String author, String avatarUrl) {
        return BotEmbed.success()
                .description(Localization.format(Xp.Embed.DESCRIPTION, Map.of("user", user, "level", level)))
                .author(author, avatarUrl)
                .build();
    }
}
