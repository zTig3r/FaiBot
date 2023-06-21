package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.OffsetDateTime;

import static de.ztiger.faibot.FaiBot.logChannel;
import static de.ztiger.faibot.utils.MessageCachingService.add;
import static de.ztiger.faibot.utils.MessageCachingService.get;

@SuppressWarnings("ConstantConditions")
public class MessageUpdate extends ListenerAdapter {

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getChannel().equals(logChannel)) return;

        try {
            Message message = event.getMessage();

            MessageEmbed embed = new EmbedBuilder()
                    .setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl())
                    .setColor(Color.ORANGE)
                    .addField("Message edited: " + event.getMessage().getJumpUrl(), "\u00A0", false)
                    .addField("Before", get(message).getContentRaw(), false)
                    .addField("After", message.getContentRaw(), false)
                    .setFooter("User ID: " + message.getAuthor().getId() + " | Message ID:" + message.getId())
                    .setTimestamp(OffsetDateTime.now())
                    .build();

            add(event.getMessage());

            logChannel.sendMessageEmbeds(embed).queue();
        } catch (Exception ignored) {
        }
    }
}
