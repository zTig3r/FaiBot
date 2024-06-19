package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

@SuppressWarnings("ConstantConditions")
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

        if (!now.minusDays(1).equals(last)) setter.resetStreak(id);
        else setter.increaseStreak(id);

        int streak = getter.getStreak(id);
        boolean streakBonus = streak % 10 == 0;
        int bonusPoints = streakBonus ? (streak / 10) * 10 : 0;

        setter.setLastReward(id, now.toString());
        setter.addPoints(id, amount + bonusPoints);

        logger.info("User {} received {} points as daily reward.", event.getUser().getEffectiveName(), amount);
        event.replyEmbeds(getEmbed("daily", Map.of("amount", amount + (streakBonus ? " *" + format(KEY + "streakBonus", Map.of("amount", bonusPoints + "*")) : ""), "streak", String.valueOf(getter.getStreak(id)), "author_name", user.getName(), "author_icon", event.getUser().getAvatarUrl()))).queue();
    }
}
