package de.ztiger.faibot.config;

public enum ConfigType {
    COLORS("colors"),
    MAIN("config"),
    EMBEDS("embeds");

    private final String fileName;

    ConfigType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
