package de.ztiger.faibot.config;

import lombok.Getter;

@Getter
public enum BotChannel {
    WELCOME("welcome"),
    LOG("log"),
    RECOMMENDATIONS("recommendations"),
    NIXOS("nixos"),
    TWITCH("twitch"),
    YOUTUBE("youtube"),
    REACTION("reaction");

    private final String configKey;

    BotChannel(String configKey) {
        this.configKey = configKey;
    }
}