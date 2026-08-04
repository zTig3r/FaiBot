package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.BotEmbed;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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

            ChannelProvider.sendEmbed(BotChannel.LOG, messageEdit(message.getJumpUrl(), get(message).getContentRaw(), message.getContentRaw(), message.getAuthor().getId(), message.getId(), message.getAuthor().getEffectiveName(), message.getAuthor().getAvatarUrl()));

            add(event.getMessage());
        } catch (Exception e) {
            logger.error("Error while processing message edit event", e);
        }
    }

    public static MessageEmbed messageEdit(String messageLink, String oldMessage, String newMessage, String userId, String messageId, String author, String avatarUrl) {
        return BotEmbed.warning()
                .boldField(Localization.format(Log.MessageEdit.DESCRIPTION, "messageLink", messageLink))
                .field(Localization.get(Log.MessageEdit.BEFORE), oldMessage)
                .field(Localization.get(Log.MessageEdit.AFTER), newMessage)
                .footer(Localization.format(Log.MessageEdit.FOOTER, "userId", userId, "messageId", messageId))
                .author(author, avatarUrl)
                .withTimestamp()
                .build();
    }
}
