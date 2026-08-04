package de.ztiger.faibot.config;

import org.simpleyaml.configuration.file.FileConfiguration;

import java.awt.*;

import static de.ztiger.faibot.FaiBot.cfgm;

public class ConfigHelper {

    public static FileConfiguration getLanguageConfig() {
        String lang = cfgm.getConfig().getString("language", "de_DE");
        return cfgm.getLanguageConfig(lang);
    }

    public static String getChannelId(String key) {
        return cfgm.getConfig().getString("channel." + key);
    }

    public static Color getColor(String key) {
        return Color.decode(cfgm.getConfig().getString("color." + key));
    }
}
