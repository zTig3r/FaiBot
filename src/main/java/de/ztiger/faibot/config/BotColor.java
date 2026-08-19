package de.ztiger.faibot.config;

import lombok.Getter;

@Getter
public enum BotColor {
    DEFAULT("default"),
    TWITCH("twitch");

    private final String configKey;

    BotColor(String configKey) {
        this.configKey = configKey;
    }
}
