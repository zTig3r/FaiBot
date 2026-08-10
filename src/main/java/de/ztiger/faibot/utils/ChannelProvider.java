package de.ztiger.faibot.utils;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.config.ConfigManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Optional;

@RequiredArgsConstructor
public class ChannelProvider {

    // TODO: Implement error if channel is not found

    private final ShardManager shardManager;
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

    public void editComponents(BotChannel channel, String messageId, MessageTopLevelComponent component) {
        getChannel(channel).ifPresent(c -> c.editMessageComponentsById(messageId, component).useComponentsV2().queue());
    }

    public void sendComponentAndCreateThread(BotChannel channel, MessageTopLevelComponent component, String threadName) {
        getChannel(channel).map(c -> c.sendMessageComponents(component).useComponentsV2().submit()
                .thenCompose(message -> message.createThreadChannel(threadName).submit()));
    }

    private Optional<GuildMessageChannel> getChannel(BotChannel channel) {
        String id = configManager.getChannelId(channel.getConfigKey());
        if (id == null || id.isEmpty()) return Optional.empty();

        GuildMessageChannel guildChannel = shardManager.getChannelById(GuildMessageChannel.class, id);
        return Optional.ofNullable(guildChannel);
    }
}