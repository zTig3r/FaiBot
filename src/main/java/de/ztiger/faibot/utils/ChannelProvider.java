package de.ztiger.faibot.utils;

import de.ztiger.faibot.FaiBot;
import de.ztiger.faibot.config.BotChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.util.Optional;
import java.util.function.Consumer;

import static de.ztiger.faibot.config.ConfigHelper.getChannelId;

public class ChannelProvider {

    public static Optional<GuildMessageChannel> getChannel(BotChannel channel) {
        String id = getChannelId(channel.getConfigKey());
        if (id == null || id.isEmpty()) return Optional.empty();

        GuildMessageChannel guildChannel = FaiBot.getShardManager().getChannelById(GuildMessageChannel.class, id);
        return Optional.ofNullable(guildChannel);
    }

    public static boolean isChannel(Channel channel, BotChannel targetChannel) {
        if (channel == null) return false;

        String targetId = getChannelId(targetChannel.getConfigKey());
        return targetId != null && !targetId.isEmpty() && targetId.equals(channel.getId());
    }

    public static void sendMessage(BotChannel channel, String text) {
        getChannel(channel).ifPresent(c -> c.sendMessage(text).queue());
    }

    public static void sendMessageWithEmbed(BotChannel channel, String text, MessageEmbed embed, Consumer<String> onSent) {
        getChannel(channel).ifPresent(c ->
                c.sendMessage(text).setEmbeds(embed).queue(message -> onSent.accept(message.getId()))
        );
    }

    public static void sendEmbed(BotChannel channel, MessageEmbed embed) {
        getChannel(channel).ifPresent(c -> c.sendMessageEmbeds(embed).queue());
    }

    public static void editMessageEmbed(BotChannel channel, String messageId, MessageEmbed embed) {
        getChannel(channel).ifPresent(c -> c.editMessageEmbedsById(messageId, embed).queue());
    }

    public static void editMessageWithEmbed(BotChannel channel, String messageId, String message, MessageEmbed embed) {
        getChannel(channel).ifPresent(c -> c.editMessageById(messageId, message).setEmbeds(embed).queue());
    }
}