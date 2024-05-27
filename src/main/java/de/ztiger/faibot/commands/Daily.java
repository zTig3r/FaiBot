package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.Colors.nixo;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

public class Daily {

    private static final String KEY = "daily.";

    public static void sendDailyReward(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        String id = user.getId();

        String lastReward = getter.getLastReward(event.getUser().getId());

        LocalDate last = LocalDate.parse((lastReward == null) ? "2000-01-01" : lastReward);
        LocalDate now = LocalDate.now();

        if (last.equals(now)) {
            event.reply(getLang(KEY + "error")).setEphemeral(true).queue();
            return;
        }

        int amount = ThreadLocalRandom.current().nextInt(1, 100);

        if(!now.minusDays(1).equals(last)) setter.resetStreak(id);
        else setter.addStreak(id);

        setter.setLastReward(id, now.toString());
        setter.addPoints(id, amount);

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(user.getName(), null, event.getUser().getAvatarUrl())
                .setTitle(getLang(KEY + "title"))
                .addField(getLang(KEY + "reward"), format(KEY + ".rewardFormat", Map.of("amount", amount)), false)
                .setFooter(format(KEY + ".streak", Map.of("streak", getter.getStreak(id))), null)
                .setColor(nixo)
                .build();

        logger.info("User " + event.getUser().getEffectiveName() + " received " + amount + " points as daily reward.");
        event.replyEmbeds(embed).queue();
    }
}
