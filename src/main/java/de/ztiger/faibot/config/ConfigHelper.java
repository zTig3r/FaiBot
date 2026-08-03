package de.ztiger.faibot.config;

import org.simpleyaml.configuration.file.FileConfiguration;

import static de.ztiger.faibot.FaiBot.cfgm;

public class ConfigHelper {

    public static FileConfiguration getAppConfig() {
        return cfgm.getConfig(ConfigType.MAIN);
    }

    public static FileConfiguration getColorsConfig() {
        return cfgm.getConfig(ConfigType.COLORS);
    }

    public static FileConfiguration getEmbedsConfig() {
        return cfgm.getConfig(ConfigType.EMBEDS);
    }

    public static FileConfiguration getLanguageConfig() {
        String lang = getAppConfig().getString("language", "de_DE");
        return cfgm.getLanguageConfig(lang);
    }

    public static String getChannelId(String key) {
        String path = "channels." + key;
        if (!getAppConfig().contains(path)) {
            path = "channel." + key;
        }
        return getAppConfig().getString(path);
    }

    /*
        public static List<String> getEmbedLines(String embedKey) {
            return getEmbedsConfig().getStringList(embedKey);
        }

    */

    public static int getColorPrice() {
        return getAppConfig().getInt("colorPrice", 750);
    }
}
