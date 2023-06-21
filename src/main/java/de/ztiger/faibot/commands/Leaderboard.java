package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;

import static de.ztiger.faibot.FaiBot.*;

@SuppressWarnings("ConstantConditions")
public class Leaderboard {

    private static final Button next = Button.secondary("next", "➡️ Nächste Seite");
    private static final Button back = Button.secondary("return", "⬅️ Vorherige Seite");
    private static final int maxPage = (int) Math.ceil((double) getShardManager().getGuildById(config.get("GUILD")).getMembers().size() / 10);

    public static void sendLeaderboardEmbed(SlashCommandInteractionEvent event) {
        ActionRow row = ActionRow.of(back, next);
        Button backButton = row.getButtons().get(0).asDisabled();
        Button nextButton = row.getButtons().get(1).asEnabled();

        MessageEmbed embed = createLeaderboardEmbed(0);
        event.replyEmbeds(embed).setActionRow(backButton, nextButton).setEphemeral(true).queue();
    }

    public static void next(ButtonInteractionEvent event) {
        int page = Integer.parseInt(event.getMessage().getEmbeds().get(0).getFooter().getText().replaceAll("[^0-9]", ""));
        ActionRow row = ActionRow.of(back, next);
        Button button = row.getButtons().get(1).asEnabled();
        if (page == maxPage) button = row.getButtons().get(1).asDisabled();

        event.editMessageEmbeds(createLeaderboardEmbed(page)).setActionRow(back, button).queue();
    }

    public static void back(ButtonInteractionEvent event) {
        int page = Integer.parseInt(event.getMessage().getEmbeds().get(0).getFooter().getText().replaceAll("[^0-9]", ""));
        ActionRow row = ActionRow.of(back, next);
        Button button = row.getButtons().get(0).asEnabled();
        if (page - 1 == 1) button = row.getButtons().get(0).asDisabled();

        event.editMessageEmbeds(createLeaderboardEmbed(page - 2)).setActionRow(button, next).queue();
    }

    private static MessageEmbed createLeaderboardEmbed(int page) {
        int i = page * 10;

        ArrayList<Member> members = new ArrayList<>(getter.getTopMembers(i));

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Leaderboard");
        embed.setFooter("Seite " + (page + 1));

        for (Member m : members) {
            if (m != null) {
                embed.addField("\u00A0", i + 1 + ") " + m.getAsMention() + " Level: " + getter.getLevel(m.getId()) + " | XP: " + getter.getXP(m.getId()) + " | Punkte: " + getter.getPoints(m.getId()) + " | Nachrichten: " + getter.getMessages(m.getId()), false);
                i++;
            }
        }

        return embed.build();
    }
}
