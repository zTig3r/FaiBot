package de.ztiger.faibot.utils;

import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Optional;

public class UserProvider {

    @Setter
    private ShardManager shardManager;

    public Optional<User> getUserById(long userId) {
        if (userId <= 0 || shardManager == null) return Optional.empty();

        return Optional.ofNullable(shardManager.getUserById(userId));
    }
}
