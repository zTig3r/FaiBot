package de.ztiger.faibot.interactions.twitch;

import de.ztiger.faibot.config.BotColor;
import de.ztiger.faibot.config.ConfigManager;
import de.ztiger.faibot.localization.keys.Twitch;
import de.ztiger.faibot.services.LocalizationService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

@RequiredArgsConstructor
public class TwitchComponents {

    private final ConfigManager configManager;
    private final LocalizationService i18n;

    public MessageEmbed getNotificationEmbed(String username, String previewUrl, String profileUrl, String profilePictureUrl, String title, String game, int viewers, String duration) {
        return new EmbedBuilder().setAuthor(username, profileUrl, profilePictureUrl)
                .setTitle(title, profileUrl)
                .addField(i18n.get(Twitch.GAME), game, true)
                .addField(i18n.get(Twitch.VIEWERS), String.valueOf(viewers), true)
                .addField(i18n.get(Twitch.DURATION), duration, true)
                .setImage(previewUrl)
                .setColor(configManager.getColor(BotColor.TWITCH))
                .build();
    }

    public MessageEmbed getEndNotificationEmbed(String username, String offlineImageUrl, String profileUrl, String profilePictureUrl, String duration) {
        return new EmbedBuilder().setAuthor(username, profileUrl, profilePictureUrl)
                .setDescription(i18n.format(Twitch.OFFLINE, "duration", duration))
                .setImage(offlineImageUrl)
                .setColor(configManager.getColor(BotColor.TWITCH))
                .build();
    }
}
