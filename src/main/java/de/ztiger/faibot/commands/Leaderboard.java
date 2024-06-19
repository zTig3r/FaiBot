package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashMap;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

@SuppressWarnings("ConstantConditions")
public class Leaderboard {

    private static final String KEY = "leaderboard.";

    private static final Button next = Button.secondary("next", getLang(KEY + "next"));
    private static final Button back = Button.secondary("return", getLang(KEY + "back"));

    private static int maxPage = 0;

    public static void sendLeaderboardEmbed(SlashCommandInteractionEvent event) {
        maxPage = (int) Math.ceil((double) getShardManager().getGuildById(config.get("GUILD")).getMembers().size() / 10);

        event.replyEmbeds(createLeaderboardEmbed(0)).setActionRow(back.asDisabled(), (maxPage > 1) ? next : next.asDisabled()).setEphemeral(true).queue();
    }

    public static void next(ButtonInteractionEvent event) {
        int page = getPage(event);

        event.editMessageEmbeds(createLeaderboardEmbed(page)).setActionRow(back, (page == maxPage) ? next.asDisabled() : next).queue();
    }

    public static void back(ButtonInteractionEvent event) {
        int page = getPage(event);

        event.editMessageEmbeds(createLeaderboardEmbed(page - 2)).setActionRow((page - 1 == 1) ? back.asDisabled() : back, next).queue();
    }

    private static MessageEmbed createLeaderboardEmbed(int page) {
        int i = page * 10;

        Map<String, String> contents = new HashMap<>();

        contents.put("page", String.valueOf(page + 1));

        for (Member m : getter.getTopMembers(i)) {
            if (m != null) {
                contents.put("field" + i, format(KEY + "format", Map.of("position", i + 1, "name", m.getAsMention(), "level", getter.getLevel(m.getId()), "xp", getter.getXP(m.getId()), "points", getter.getPoints(m.getId()), "messages", getter.getMessages(m.getId()))));
                i++;
            }
        }

        return getEmbed("leaderboard", contents);
    }

    private static int getPage(ButtonInteractionEvent event) {
        return Integer.parseInt(event.getMessage().getEmbeds().get(0).getFooter().getText().replaceAll("[^0-9]", ""));
    }
}
