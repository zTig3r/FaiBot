package de.ztiger.faibot.interactions.twitch;

import de.ztiger.faibot.config.BotColor;
import de.ztiger.faibot.config.ConfigManager;
import de.ztiger.faibot.localization.keys.Twitch;
import de.ztiger.faibot.services.LocalizationService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;

@RequiredArgsConstructor
public class TwitchComponents {

    private final ConfigManager configManager;
    private final LocalizationService i18n;

    public Container notification(String previewUrl, String profileUrl, String profilePictureUrl, String title, String game, int viewers, String duration) {
        return Container.of(
                Section.of(
                        Thumbnail.fromUrl(profilePictureUrl),
                        TextDisplay.of("# [" + title + "](" + profileUrl + ")")
                ),
                TextDisplay.of(i18n.format(Twitch.GAME, "game", game)),
                TextDisplay.of(i18n.format(Twitch.VIEWERS, "viewers", viewers)),
                TextDisplay.of(i18n.format(Twitch.DURATION, "duration", duration)),
                MediaGallery.of(MediaGalleryItem.fromUrl(previewUrl))
        ).withAccentColor(configManager.getColor(BotColor.TWITCH));
    }

    public Container endNotification(String profileUrl, String profilePictureUrl, String duration) {
        return Container.of(
                Section.of(
                        Thumbnail.fromUrl(profilePictureUrl),
                        TextDisplay.of(i18n.format(Twitch.OFFLINE, "duration", duration))
                )

        ).withAccentColor(configManager.getColor(BotColor.TWITCH));
    }
}
