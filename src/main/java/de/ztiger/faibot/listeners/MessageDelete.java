package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.services.MessageCachingService;
import de.ztiger.faibot.utils.UserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MessageDelete extends ListenerAdapter {

    private final ChannelProvider channelProvider;
    private final MessageCachingService messageCachingService;
    private final UserProvider userProvider;
    private final LocalizationService i18n;

    @Override
    public void onMessageDelete(@NonNull MessageDeleteEvent event) {
        try {
            long messageId = event.getMessageIdLong();

            MessageCachingService.CachedMessage message = messageCachingService.get(messageId);

            if (message == null) {
                log.warn("Message with ID {} not found in cache", messageId);
                return;
            }

            messageCachingService.remove(messageId);

            Optional<GuildMessageChannel> channelOpt = channelProvider.getChannelById(message.channelId());
            Optional<User> userOpt = userProvider.getUserById(message.authorId());

            if (channelOpt.isEmpty() || userOpt.isEmpty()) {
                log.warn("Could not resolve channel or user for cached message ID {}", messageId);
                return;
            }

            GuildMessageChannel channel = channelOpt.get();
            User user = userOpt.get();

            channelProvider.sendComponent(BotChannel.LOG, messageDelete(channel.getAsMention(), message.content(), user.getIdLong(), message.id(), user.getEffectiveName()));
        } catch (Exception e) {
            log.error("Error while processing message delete event", e);
        }
    }

    private Container messageDelete(String channel, String content, long userId, long messageId, String author) {
        return Container.of(
                TextDisplay.of(i18n.format(Log.Message.Delete.TITLE, "channel", channel)),
                TextDisplay.of(i18n.format(Log.Message.SENDER, "user", author)),
                TextDisplay.of(content),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(i18n.format(Log.Message.FOOTER, "userid", userId, "messageid", messageId))
        ).withAccentColor(Color.RED);
    }
}
