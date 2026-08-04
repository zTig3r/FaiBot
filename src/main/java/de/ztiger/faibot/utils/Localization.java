package de.ztiger.faibot.utils;

import de.ztiger.faibot.localization.keys.Time;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.config.ConfigHelper.getLanguageConfig;

public class Localization {

    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static String get(String key) {
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

    public static String format(String key, Object... args) {
        String message = get(key);
        if (args == null || args.length == 0) {
            return message;
        }

        if (args.length % 2 != 0) {
            logger.warn("Varargs passed to Lang.format for key '{}' are not balanced pairs!", key);
            return message;
        }

        for (int i = 0; i < args.length; i += 2) {
            String placeholder = "{" + args[i] + "}";
            String value = String.valueOf(args[i + 1]);
            message = message.replace(placeholder, value);
        }

        return message;
    }

    public static String formatPeriod( Period period) {
        List<String> parts = new ArrayList<>();
        addPart(parts, period.getYears(), Time.YEAR, Time.YEARS);
        addPart(parts, period.getMonths(), Time.MONTH, Time.MONTHS);
        addPart(parts, period.getDays(), Time.DAY, Time.DAYS);

        return parts.isEmpty() ? format(Time.DAYS, "count", 0) : String.join(", ", parts);
    }

    private static void addPart(List<String> parts, int count, String singularKey, String pluralKey) {
        if (count > 0) {
            parts.add(format(count == 1 ? singularKey : pluralKey, "count", count));
        }
    }

    // TODO: Implement in some way for config reloading
    public static void clearCache() {
        cache.clear();
    }
}
