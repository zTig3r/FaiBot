package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.services.LocalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

import static de.ztiger.faibot.utils.MessageCachingService.add;
import static de.ztiger.faibot.utils.MessageCachingService.get;

@Slf4j
@RequiredArgsConstructor
public class MessageEdit extends ListenerAdapter {

    private final ChannelProvider channelProvider;
    private final LocalizationService i18n;

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        // TODO: Add blacklist for channels
        // if (event.getChannel().equals(logChannel) || event.getAuthor().isBot()) return;

        try {
            Message message = event.getMessage();

            channelProvider.sendComponent(BotChannel.LOG, messageEdit(message.getJumpUrl(), get(message).getContentRaw(), message.getContentRaw(), message.getAuthor().getId(), message.getId(), message.getAuthor().getEffectiveName()));

            add(event.getMessage());
        } catch (Exception e) {
            log.error("Error while processing message edit event", e);
        }
    }

    private Container messageEdit(String messageLink, String oldMessage, String newMessage, String userId, String messageId, String author) {
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
