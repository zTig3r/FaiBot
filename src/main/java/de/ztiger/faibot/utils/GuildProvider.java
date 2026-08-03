package de.ztiger.faibot.utils;

import de.ztiger.faibot.FaiBot;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Optional;

import static de.ztiger.faibot.config.ConfigHelper.getAppConfig;

public class GuildProvider {

    public static Optional<Guild> getMainGuild() {
        String guildId = getAppConfig().getString("guild");

        if (guildId == null || guildId.isEmpty()) return Optional.empty();

        return Optional.ofNullable(FaiBot.getShardManager().getGuildById(guildId));
    }
}
