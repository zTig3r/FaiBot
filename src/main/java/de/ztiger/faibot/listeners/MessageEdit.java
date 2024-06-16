package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.logChannel;
import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.MessageCachingService.add;
import static de.ztiger.faibot.utils.MessageCachingService.get;

@SuppressWarnings("ConstantConditions")
public class MessageEdit extends ListenerAdapter {

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getChannel().equals(logChannel)) return;

        try {
            Message message = event.getMessage();

            Map<String, String> contents = Map.of("msgLink", message.getJumpUrl(), "oldMessage", get(message).getContentRaw(), "newMessage", message.getContentRaw(), "uID", message.getAuthor().getId(), "mID", message.getId(), "author_name", message.getAuthor().getEffectiveName(), "author_icon", message.getAuthor().getAvatarUrl());

            add(event.getMessage());

            logChannel.sendMessageEmbeds(getEmbed("messageEdit", contents, Color.YELLOW)).queue();
        } catch (Exception e) {
            logger.error("Error while processing message edit event", e);
        }
    }
}
