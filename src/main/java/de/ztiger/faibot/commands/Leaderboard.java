package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

public class Leaderboard implements ICommand {

    private static final String KEY = "leaderboard.";

    private static final Button next = Button.secondary("next", getLang(KEY + "next"));
    private static final Button back = Button.secondary("return", getLang(KEY + "back"));

    private static int maxPage = 0;

    @Override
    public CommandData getCommandData() {
        return Commands.slash("leaderboard", "Zeigt das Leaderboard an");
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        maxPage = (int) Math.ceil((double) getShardManager().getGuildById(config.get("GUILD")).getMembers().size() / 10);

        event.replyComponents(ActionRow.of(back.asDisabled(), (maxPage > 1) ? next : next.asDisabled())).setEmbeds(createLeaderboardEmbed(0)).setEphemeral(true).queue();
    }

    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String id = event.getButton().getCustomId();
        int page = getPage(event);

        if (id.equals("next")) {
            event.editComponents(ActionRow.of((page == 1) ? back.asDisabled() : back, (page == maxPage - 1) ? next.asDisabled() : next)).setEmbeds(createLeaderboardEmbed(page)).queue();
        } else if (id.equals("return")) {
            event.editComponents(ActionRow.of((page - 1 == 1) ? back.asDisabled() : back, next)).setEmbeds(createLeaderboardEmbed(page - 2)).queue();
        }
    }

    private static MessageEmbed createLeaderboardEmbed(int page) {
        int i = page * 10;

        LinkedHashMap<String, String> contents = new LinkedHashMap<>();

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
