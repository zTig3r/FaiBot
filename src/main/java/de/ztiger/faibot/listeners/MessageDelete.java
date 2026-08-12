package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.utils.MessageCachingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

@Slf4j
@RequiredArgsConstructor
public class MessageDelete extends ListenerAdapter {

    private final ChannelProvider channelProvider;
    private final LocalizationService i18n;

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        // TODO: Add blacklist for channels
        // if (event.getChannel() == logChannel) return;

        try {
            Message message = MessageCachingService.get(event.getMessageId(), event.getChannel().getId());
            MessageCachingService.remove(message);

            channelProvider.sendComponent(BotChannel.LOG, messageDelete(message.getChannel().getAsMention(), message.getContentRaw(), message.getAuthor().getId(), message.getId(), message.getAuthor().getEffectiveName()));
        } catch (Exception e) {
            log.error("Error while processing message delete event", e);
        }
    }

    private Container messageDelete(String channel, String content, String userId, String messageId, String author) {
        return Container.of(
                TextDisplay.of(i18n.format(Log.Message.Delete.TITLE, "channel", channel)),
                TextDisplay.of(i18n.format(Log.Message.SENDER, "user", author)),
                TextDisplay.of(content),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(i18n.format(Log.Message.FOOTER, "userid", userId, "messageid", messageId))
        ).withAccentColor(Color.RED);
    }
}
