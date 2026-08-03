package de.ztiger.faibot.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.config.ConfigHelper.getLanguageConfig;

public class Lang {

    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static String getLang(String key) {
        return cache.computeIfAbsent(key, k -> {
            try {
                String value = getLanguageConfig().getString(k);
                return (value != null) ? value : k;
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

    // TODO: Implement in some way for config reloading
    public static void clearCache() {
        cache.clear();
    }
}
