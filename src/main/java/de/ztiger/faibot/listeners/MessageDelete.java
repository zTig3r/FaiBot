package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.OffsetDateTime;

import static de.ztiger.faibot.FaiBot.logChannel;
import static de.ztiger.faibot.utils.MessageCachingService.get;
import static de.ztiger.faibot.utils.MessageCachingService.remove;

@SuppressWarnings("ConstantConditions")
public class MessageDelete extends ListenerAdapter {
    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (event.getChannel() == logChannel) return;

        try {
            Message message = get(event.getMessageId(), event.getChannel().getId());

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(message.getAuthor().getEffectiveName(), null, message.getAuthor().getAvatarUrl())
                    .setColor(Color.RED)
                    .addField("Message deleted in " + message.getChannel().getAsMention(), "\u00A0 " + message.getContentRaw(), false)
                    .setFooter("User ID: " + message.getAuthor().getId() + " | Message ID:" + message.getId())
                    .setTimestamp(OffsetDateTime.now());

            if (!message.getAttachments().isEmpty()) embed.setImage(message.getAttachments().get(0).getUrl());

            remove(message);

            logChannel.sendMessageEmbeds(embed.build()).queue();
        } catch (Exception ignored) {
        }
    }
}
