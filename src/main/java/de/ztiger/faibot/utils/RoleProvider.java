package de.ztiger.faibot.utils;

import de.ztiger.faibot.config.BotRole;
import de.ztiger.faibot.config.ConfigManager;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Optional;

@RequiredArgsConstructor
public class RoleProvider {

    @Setter
    private ShardManager shardManager;

    private final ConfigManager configManager;

    public Optional<Role> getRole(BotRole role) {
        String roleId = configManager.getRoleId(role.getConfigKey());

        if (roleId == null || roleId.isEmpty()) return Optional.empty();

        return Optional.ofNullable(shardManager.getRoleById(roleId));
    }
}
