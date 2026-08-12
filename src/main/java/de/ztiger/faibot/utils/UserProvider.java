package de.ztiger.faibot.utils;

import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Optional;

public class UserProvider {

    @Setter
    private ShardManager shardManager;

    public Optional<User> getUserById(long userId) {
        if (userId <= 0) return Optional.empty();

        User user = shardManager.getUserById(userId);
        return Optional.ofNullable(user);
    }
}
