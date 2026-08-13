package de.ztiger.faibot.utils;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.config.ConfigManager;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Optional;

@RequiredArgsConstructor
public class ChannelProvider {

    // TODO: Implement error if channel is not found

    @Setter
    private ShardManager shardManager;

    private final ConfigManager configManager;

    public boolean isChannel(Channel channel, BotChannel targetChannel) {
        if (channel == null) return false;

        String targetId = configManager.getChannelId(targetChannel.getConfigKey());
        return targetId != null && !targetId.isEmpty() && targetId.equals(channel.getId());
    }

    public void sendMessage(BotChannel channel, String text) {
        getChannel(channel).ifPresent(c -> c.sendMessage(text).queue());
    }

    public void sendMessage(BotChannel channel, MessageCreateData data) {
        getChannel(channel).ifPresent(c -> c.sendMessage(data).queue());
    }

    public void sendComponent(BotChannel channel, MessageTopLevelComponent component) {
        getChannel(channel).ifPresent(c -> c.sendMessageComponents(component).useComponentsV2().queue());
    }

    public long sendComponentAndGetId(BotChannel channel, MessageTopLevelComponent component) {
        return getChannel(channel).map(c -> c.sendMessageComponents(component).useComponentsV2().submit()
                .thenApply(ISnowflake::getIdLong).join()).orElse(-1L);
    }

    public void editComponents(BotChannel channel, long messageId, MessageTopLevelComponent component) {
        getChannel(channel).ifPresent(c -> c.editMessageComponentsById(messageId, component).useComponentsV2().queue());
    }

    public void sendComponentAndCreateThread(BotChannel channel, MessageTopLevelComponent component, String threadName) {
        getChannel(channel).map(c -> c.sendMessageComponents(component).useComponentsV2().submit()
                .thenCompose(message -> message.createThreadChannel(threadName).submit()));
    }

    public Optional<GuildMessageChannel> getChannelById(long channelId) {
        if (channelId <= 0) return Optional.empty();

        GuildMessageChannel guildChannel = shardManager.getChannelById(GuildMessageChannel.class, channelId);
        return Optional.ofNullable(guildChannel);
    }

    private Optional<GuildMessageChannel> getChannel(BotChannel channel) {
        String id = configManager.getChannelId(channel.getConfigKey());
        if (id == null || id.isEmpty()) return Optional.empty();

        GuildMessageChannel guildChannel = shardManager.getChannelById(GuildMessageChannel.class, id);
        return Optional.ofNullable(guildChannel);
    }

    /*
     * Only use embeds for messages that really need them, otherwise use ComponentsV2.
     */

    public long sendEmbedAndGetId(BotChannel channel, String message, MessageEmbed embed) {
        return getChannel(channel).map(c -> c.sendMessage(message).setEmbeds(embed).submit()
                .thenApply(ISnowflake::getIdLong).join()).orElse(-1L);
    }

    public void editEmbed(BotChannel channel, long messageId, MessageEmbed embed) {
        getChannel(channel).ifPresent(c -> c.editMessageEmbedsById(messageId, embed).queue());
    }

    public void editMessageWithEmbed(BotChannel channel, long messageId, String message, MessageEmbed embed) {
        getChannel(channel).ifPresent(c -> c.editMessageById(messageId, message).setEmbeds(embed).queue());
    }
}