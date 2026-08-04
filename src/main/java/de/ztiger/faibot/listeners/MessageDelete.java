package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.Localization;
import de.ztiger.faibot.utils.MessageCachingService;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

import static de.ztiger.faibot.FaiBot.logger;

public class MessageDelete extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        // TODO: Add blacklist for channels
        // if (event.getChannel() == logChannel) return;

        try {
            Message message = MessageCachingService.get(event.getMessageId(), event.getChannel().getId());
            MessageCachingService.remove(message);

            ChannelProvider.getChannel(BotChannel.LOG).ifPresent(channel -> {
                channel.sendMessageComponents(messageDelete(message.getChannel().getAsMention(), message.getContentRaw(), message.getAuthor().getId(), message.getId(), message.getAuthor().getEffectiveName())).useComponentsV2().queue();
            });
        } catch (Exception e) {
            logger.error("Error while processing message delete event", e);
        }
    }

    private Container messageDelete(String channel, String content, String userId, String messageId, String author) {
        return Container.of(
                TextDisplay.of(Localization.format(Log.Message.Delete.TITLE, "channel", channel)),
                TextDisplay.of(Localization.format(Log.Message.SENDER, "user", author)),
                TextDisplay.of(content),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(Localization.format(Log.Message.FOOTER, "userId", userId, "messageId", messageId))
        ).withAccentColor(Color.RED);
    }
}
