package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.utils.MessageCachingService.add;
import static de.ztiger.faibot.utils.MessageCachingService.get;

public class MessageEdit extends ListenerAdapter {

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        // TODO: Add blacklist for channels
        // if (event.getChannel().equals(logChannel) || event.getAuthor().isBot()) return;

        try {
            Message message = event.getMessage();

            ChannelProvider.getChannel(BotChannel.LOG).ifPresent(channel -> {
                channel.sendMessageComponents(messageEdit(message.getJumpUrl(), get(message).getContentRaw(), message.getContentRaw(), message.getAuthor().getId(), message.getId(), message.getAuthor().getEffectiveName())).useComponentsV2().queue();
            });

            add(event.getMessage());
        } catch (Exception e) {
            logger.error("Error while processing message edit event", e);
        }
    }

    private Container messageEdit(String messageLink, String oldMessage, String newMessage, String userId, String messageId, String author) {
        return Container.of(
                TextDisplay.of(Localization.format(Log.Message.Edit.TITLE, "messageLink", messageLink)),
                TextDisplay.of(Localization.format(Log.Message.SENDER, "user", author)),
                TextDisplay.of(Localization.format(Log.Message.Edit.BEFORE, "message", oldMessage)),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(Localization.format(Log.Message.Edit.AFTER, "message", newMessage)),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(Localization.format(Log.Message.FOOTER, "userId", userId, "messageId", messageId))
        ).withAccentColor(Color.YELLOW);
    }
}
