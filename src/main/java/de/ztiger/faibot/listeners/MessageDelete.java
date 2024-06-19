package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.logChannel;
import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.MessageCachingService.get;
import static de.ztiger.faibot.utils.MessageCachingService.remove;

@SuppressWarnings("ConstantConditions")
public class MessageDelete extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (event.getChannel() == logChannel) return;

        try {
            Message message = get(event.getMessageId(), event.getChannel().getId());

            Map<String, String> contents = Map.of("channel", message.getChannel().getAsMention(), "message", message.getContentRaw(), "uID", message.getAuthor().getId(), "mID", message.getId(), "author_name", message.getAuthor().getEffectiveName(), "author_icon", message.getAuthor().getAvatarUrl());
            remove(message);

            logChannel.sendMessageEmbeds(getEmbed("messageDelete", contents, Color.RED)).queue();
        } catch (Exception e) {
            logger.error("Error while processing message delete event", e);
        }
    }
}
