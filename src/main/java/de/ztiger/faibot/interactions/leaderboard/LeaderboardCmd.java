package de.ztiger.faibot.interactions.leaderboard;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IButtonHandler;
import de.ztiger.faibot.utils.GuildProvider;
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

public class LeaderboardCmd implements ICommand, IButtonHandler {

    private static final String PREFIX = "leaderboard";
    private static final String KEY = "leaderboard.";

    private static final Button next = Button.secondary("next", getLang(KEY + "next"));
    private static final Button back = Button.secondary("return", getLang(KEY + "back"));

    private static int maxPage = 0;

    @Override
    public CommandData getCommandData() {
        return Commands.slash("leaderboard", "Zeigt das Leaderboard an");
    }

    @Override
    public String getComponentPrefix() {
        return PREFIX;
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        GuildProvider.getMainGuild().ifPresent(guild -> maxPage = (int) Math.ceil((double) guild.getMembers().size() / 10));

        event.replyComponents(ActionRow.of(back.asDisabled(), (maxPage > 1) ? next : next.asDisabled())).setEmbeds(createLeaderboardEmbed(0)).setEphemeral(true).queue();
    }

    @Override
    public void handleButton(ButtonInteractionEvent event, String action, String payload) {
        int page = getPage(event);

        switch (action) {
            case "next" -> handleNext(event, page);
            case "return" -> handleReturn(event, page);
            default -> logger.warn("Unknown leaderboard action: {}", action);
        }
    }

    private void handleNext(ButtonInteractionEvent event, int page) {
        event.editComponents(ActionRow.of((page == 1) ? back.asDisabled() : back, (page == maxPage - 1) ? next.asDisabled() : next)).setEmbeds(createLeaderboardEmbed(page)).queue();
    }

    private void handleReturn(ButtonInteractionEvent event, int page) {
        event.editComponents(ActionRow.of((page == 1) ? back.asDisabled() : back, (page == maxPage - 1) ? next.asDisabled() : next)).setEmbeds(createLeaderboardEmbed(page)).queue();
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
        return Integer.parseInt(event.getMessage().getEmbeds().getFirst().getFooter().getText().replaceAll("[^0-9]", ""));
    }
}
