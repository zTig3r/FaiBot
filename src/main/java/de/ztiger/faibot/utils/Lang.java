package de.ztiger.faibot.utils;

import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.logger;

public class Lang {

    private static HashMap<String, String> cache = new HashMap<>();

    public static String getLang(String key) {
        if (cache.containsKey(key)) return cache.get(key);

        try {
            FileConfiguration lang = YamlConfiguration.loadConfiguration(new File(Lang.class.getClassLoader().getResource("de_DE.yml").toURI()));

            Object value = lang.get(key);

            cache.put(key, value.toString());
            return value.toString();

        } catch (Exception e) {
            logger.error("Error while loading language file: " + e.getMessage());
        }

        return key;
    }

    public static String format(String key, Map<String, Object> replacements) {
        String lang = getLang(key);

        for (String replacement : replacements.keySet()) {
            lang = lang.replace("{" + replacement + "}", replacements.get(replacement).toString());
        }

        return lang;
    }
}
