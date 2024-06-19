package de.ztiger.faibot.utils;

import java.util.HashMap;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.cfgm;
import static de.ztiger.faibot.FaiBot.logger;

public class Lang {

    private static final HashMap<String, String> cache = new HashMap<>();

    public static String getLang(String key) {
        return cache.computeIfAbsent(key, k -> {
            try {
                return cfgm.getConfig(cfgm.getConfig("config").getString("language")).getString(k);
            } catch (Exception e) {
                logger.error("Error while loading language file: {}", e.getMessage());
                return k;
            }
        });
    }

    public static String format(String key, Map<String, Object> replacements) {
        String lang = getLang(key);

        for (String replacement : replacements.keySet()) {
            lang = lang.replace("{" + replacement + "}", replacements.get(replacement).toString());
        }

        return lang;
    }
}
