package de.ztiger.faibot.utils;

import de.ztiger.faibot.config.ConfigManager;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Optional;

@RequiredArgsConstructor
public class GuildProvider {

    private final ConfigManager configManager;

    @Setter
    private ShardManager shardManager;

    public Optional<Guild> getMainGuild() {
        String guildId = configManager.getGuildId();

        if (guildId == null || guildId.isEmpty() || shardManager == null) return Optional.empty();

        return Optional.ofNullable(shardManager.getGuildById(guildId));
    }
}
