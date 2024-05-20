package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static de.ztiger.faibot.FaiBot.*;

public class Daily {

    public static void sendDailyReward(SlashCommandInteractionEvent event) {
        String last = getter.getLastReward(event.getUser().getId());
        LocalDate now = LocalDate.now();

        if (last != null) {
            LocalDate nextReward = LocalDate.parse(last).plusDays(1);

            if (now.isBefore(nextReward)) {
                event.reply("Du hast deine tägliche Belohnung bereits erhalten!").setEphemeral(true).queue();
                return;
            }
        }

        int amount = ThreadLocalRandom.current().nextInt(1, 100);

        setter.setLastReward(event.getUser().getId(), now.toString());
        setter.addPoints(event.getUser().getId(), amount);

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl())
                .setTitle("Tägliche Belohnung")
                .addField("Belohnung", amount + " Punkte", false)
                .setColor(Color.decode("#94c6f3"))
                .build();

        logger.info("User " + event.getUser().getEffectiveName() + " received " + amount + " points as daily reward.");
        event.replyEmbeds(embed).queue();
    }
}
