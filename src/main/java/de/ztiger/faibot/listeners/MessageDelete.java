package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.BotEmbed;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.Localization;
import de.ztiger.faibot.utils.MessageCachingService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static de.ztiger.faibot.FaiBot.logger;

public class MessageDelete extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        // TODO: Add blacklist for channels
        // if (event.getChannel() == logChannel) return;

        try {
            Message message = MessageCachingService.get(event.getMessageId(), event.getChannel().getId());
            MessageCachingService.remove(message);

            ChannelProvider.sendEmbed(BotChannel.LOG, messageDelete(message.getChannel().getAsMention(), message.getContentRaw(), message.getAuthor().getId(), message.getId(), message.getAuthor().getEffectiveName(), message.getAuthor().getAvatarUrl()));
        } catch (Exception e) {
            logger.error("Error while processing message delete event", e);
        }
    }

    private static MessageEmbed messageDelete(String channel, String content, String userId, String messageId, String author, String avatarUrl) {
        return BotEmbed.error()
                .field(Localization.format(Log.MessageDelete.DESCRIPTION, "channel", channel), content)
                .footer(Localization.format(Log.MessageDelete.FOOTER, "userId", userId, "messageId", messageId))
                .author(author, avatarUrl)
                .withTimestamp()
                .build();
    }
}
