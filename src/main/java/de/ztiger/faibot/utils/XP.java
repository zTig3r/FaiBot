package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.FaiBot.botChannel;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.getLang;

public class XP {

    private static final HashMap<Member, Long> userTimer = new HashMap<>();

    public static int getLastLevelsXP(int level) {
        return IntStream.rangeClosed(0, level).map(XP::calcXP).sum();
    }

    public static int calcXP(int level) {
        return 5 * (level * level) + (50 * level) + 100;
    }

    @SuppressWarnings("ConstantConditions")
    public static void checkLevelUp(Member member) {
        int level = getter.getLevel(member.getId());
        int xpForNextLevel = calcXP(level);
        int xp = getter.getXP(member.getId()) - getLastLevelsXP(level - 1);

        if (xp >= xpForNextLevel) {
            setter.addLevel(member.getId());
            level++;

            Map<String, String> contents = Map.of("user", member.getAsMention(), "level", String.valueOf(level), "author_name", getLang("xp.levelUp"), "author_icon", member.getUser().getAvatarUrl());

            logger.info("{} reached level {}!", member.getEffectiveName(), level);
            botChannel.sendMessageEmbeds(getEmbed("levelUp", contents, Color.GREEN)).queue();
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
}
