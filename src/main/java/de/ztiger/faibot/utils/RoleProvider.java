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

    private final ConfigManager configManager;

    @Setter
    private ShardManager shardManager;

    public Optional<Role> getRole(BotRole role) {
        String roleId = configManager.getRoleId(role.getConfigKey());

        if (roleId == null || roleId.isEmpty()) return Optional.empty();

        return Optional.ofNullable(shardManager.getRoleById(roleId));
    }
}
