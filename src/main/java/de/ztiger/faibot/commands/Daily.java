package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static de.ztiger.faibot.FaiBot.*;

public class Daily {

    public static void sendDailyReward(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        String id = user.getId();

        String lastReward = getter.getLastReward(event.getUser().getId());

        LocalDate last = LocalDate.parse((lastReward == null) ? "2000-01-01" : lastReward);
        LocalDate now = LocalDate.now();

        if (last.equals(now)) {
            event.reply("Du hast deine tägliche Belohnung bereits erhalten!").setEphemeral(true).queue();
            return;
        }

        int amount = ThreadLocalRandom.current().nextInt(1, 100);

        if(!now.minusDays(1).equals(last)) setter.resetStreak(id);
        else setter.addStreak(id);

        setter.setLastReward(id, now.toString());
        setter.addPoints(id, amount);

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(user.getName(), null, event.getUser().getAvatarUrl())
                .setTitle("Tägliche Belohnung")
                .addField("Belohnung", amount + " Punkte", false)
                .setFooter("Streak: " + getter.getStreak(id), null)
                .setColor(Color.decode("#94c6f3"))
                .build();

        logger.info("User " + event.getUser().getEffectiveName() + " received " + amount + " points as daily reward.");
        event.replyEmbeds(embed).queue();
    }
}
