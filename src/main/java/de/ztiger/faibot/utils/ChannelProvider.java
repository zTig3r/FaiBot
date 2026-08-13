package de.ztiger.faibot.utils;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.config.ConfigManager;
import de.ztiger.faibot.exceptions.ChannelNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ChannelProvider {

    @Setter
    private ShardManager shardManager;

    private final ConfigManager configManager;

    public boolean isChannel(Channel channel, BotChannel targetChannel) {
        if (channel == null) return false;

        String targetId = configManager.getChannelId(targetChannel.getConfigKey());
        return targetId != null && !targetId.isEmpty() && targetId.equals(channel.getId());
    }

    public void sendMessage(BotChannel channel, String text) {
        getRequiredChannel(channel).sendMessage(text).queue();
    }

    public void sendMessage(BotChannel channel, MessageCreateData data) {
        getRequiredChannel(channel).sendMessage(data).queue();
    }

    public void sendComponent(BotChannel channel, MessageTopLevelComponent component) {
        getRequiredChannel(channel).sendMessageComponents(component).useComponentsV2().queue();
    }

    public long sendComponentAndGetId(BotChannel channel, MessageTopLevelComponent component) {
        return getRequiredChannel(channel).sendMessageComponents(component).useComponentsV2().submit()
                .thenApply(ISnowflake::getIdLong).join();
    }

    public void editComponents(BotChannel channel, long messageId, MessageTopLevelComponent component) {
        getRequiredChannel(channel).editMessageComponentsById(messageId, component).useComponentsV2().queue();
    }

    public void sendComponentAndCreateThread(BotChannel channel, MessageTopLevelComponent component, String threadName) {
        getRequiredChannel(channel).sendMessageComponents(component).useComponentsV2().submit()
                .thenCompose(message -> message.createThreadChannel(threadName).submit());
    }

    /*
     * Only use embeds for messages that really need them, otherwise use ComponentsV2.
     */

    public long sendEmbedAndGetId(BotChannel channel, String message, MessageEmbed embed) {
        return getRequiredChannel(channel).sendMessage(message).setEmbeds(embed).submit()
                .thenApply(ISnowflake::getIdLong).join();
    }

    public void editEmbed(BotChannel channel, long messageId, MessageEmbed embed) {
        getRequiredChannel(channel).editMessageEmbedsById(messageId, embed).queue();
    }

    public void editMessageWithEmbed(BotChannel channel, long messageId, String message, MessageEmbed embed) {
        getRequiredChannel(channel).editMessageById(messageId, message).setEmbeds(embed).queue();
    }

    public Optional<GuildMessageChannel> getChannelById(long channelId) {
        if (channelId <= 0) return Optional.empty();

        GuildMessageChannel guildChannel = shardManager.getChannelById(GuildMessageChannel.class, channelId);
        return Optional.ofNullable(guildChannel);
    }

    public GuildMessageChannel getRequiredChannel(BotChannel channel) {
        String id = configManager.getChannelId(channel.getConfigKey());
        if (id == null || id.isBlank()) {
            throw new ChannelNotFoundException(channel, "Channel ID is not configured in ConfigManager");
        }

        GuildMessageChannel guildChannel = shardManager.getChannelById(GuildMessageChannel.class, id);
        if (guildChannel == null) {
            throw new ChannelNotFoundException(channel, "No channel found on Discord for ID: " + id);
        }

        return guildChannel;
    }
}