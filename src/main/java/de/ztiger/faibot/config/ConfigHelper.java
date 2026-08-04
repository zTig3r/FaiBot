package de.ztiger.faibot.config;

import org.simpleyaml.configuration.file.FileConfiguration;

import java.awt.*;

import static de.ztiger.faibot.FaiBot.cfgm;

public class ConfigHelper {

    public static FileConfiguration getAppConfig() {
        return cfgm.getConfig(ConfigType.MAIN);
    }

    public static FileConfiguration getColorsConfig() {
        return cfgm.getConfig(ConfigType.COLORS);
    }

    public static FileConfiguration getLanguageConfig() {
        String lang = getAppConfig().getString("language", "de_DE");
        return cfgm.getLanguageConfig(lang);
    }

    public static String getChannelId(String key) {
        return getAppConfig().getString("channel." + key);
    }

    public static Color getColor(String key) {
        return Color.decode(getAppConfig().getString("color." + key));
    }

    public static int getColorPrice() {
        return getAppConfig().getInt("colorPrice", 750);
    }
}
