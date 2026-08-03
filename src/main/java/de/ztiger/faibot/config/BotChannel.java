package de.ztiger.faibot.config;

public enum BotChannel {
    WELCOME("welcome"),
    LOG("log"),
    RECOMMENDATIONS("recommendations"),
    BOT("bot"),
    TWITCH("twitch"),
    YOUTUBE("youtube"),
    REACTION("reaction");

    private final String configKey;

    BotChannel(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }
}