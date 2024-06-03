package de.ztiger.faibot.utils;

import java.util.HashMap;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.cfgm;
import static de.ztiger.faibot.FaiBot.logger;

public class Lang {

    private static final HashMap<String, String> cache = new HashMap<>();

    public static String getLang(String key) {
        if (cache.containsKey(key)) return cache.get(key);

        try {
            String value = cfgm.getConfig(cfgm.getConfig("config").getString("language")).getString(key);

            cache.put(key, value);
            return value;

        } catch (Exception e) {
            logger.error("Error while loading language file: {}", e.getMessage());
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
