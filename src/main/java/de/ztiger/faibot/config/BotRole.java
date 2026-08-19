package de.ztiger.faibot.config;

import lombok.Getter;

@Getter
public enum BotRole {
    NIXOS("nixos"),
    TWITCH("twitch"),
    YOUTUBE("youtube");

    private final String configKey;

    BotRole(String configKey) {
        this.configKey = configKey;
    }
}
