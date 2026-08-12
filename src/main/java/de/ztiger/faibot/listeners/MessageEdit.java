package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.services.MessageCachingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

@Slf4j
@RequiredArgsConstructor
public class MessageEdit extends ListenerAdapter {

    private final ChannelProvider channelProvider;
    private final MessageCachingService messageCachingService;
    private final LocalizationService i18n;

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getAuthor().isBot()) return;

        try {
            Message message = event.getMessage();

            MessageCachingService.CachedMessage oldMessage = messageCachingService.get(message.getIdLong());

            if(oldMessage == null) {
                log.warn("Message with ID {} not found in cache", message.getIdLong());
                return;
            }

            channelProvider.sendComponent(BotChannel.LOG, messageEdit(message.getJumpUrl(),
                    oldMessage.content(), message.getContentRaw(), message.getAuthor().getIdLong(),
                    message.getIdLong(), message.getAuthor().getEffectiveName()));

            messageCachingService.add(event.getMessage());
        } catch (Exception e) {
            log.error("Error while processing message edit event", e);
        }
    }

    private Container messageEdit(String messageLink, String oldMessage, String newMessage, long userId, long messageId, String author) {
        return Container.of(
                TextDisplay.of(i18n.format(Log.Message.Edit.TITLE, "messagelink", messageLink)),
                TextDisplay.of(i18n.format(Log.Message.SENDER, "user", author)),
                TextDisplay.of(i18n.format(Log.Message.Edit.BEFORE, "message", oldMessage)),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(i18n.format(Log.Message.Edit.AFTER, "message", newMessage)),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(i18n.format(Log.Message.FOOTER, "userid", userId, "messageid", messageId))
        ).withAccentColor(Color.YELLOW);
    }
}
