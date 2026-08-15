package de.ztiger.faibot.services;

import de.ztiger.faibot.config.ConfigManager;
import de.ztiger.faibot.localization.keys.Time;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class LocalizationService {

    private static final Map<String, String> cache = new ConcurrentHashMap<>();
    private static volatile Locale cachedLocale;

    private final ConfigManager configManager;

    public Locale getLocale() {
        if (cachedLocale == null) {
            synchronized (LocalizationService.class) {
                if (cachedLocale == null) {
                    String langTag = configManager.getLanguage().replace('_', '-');

                    cachedLocale = Locale.forLanguageTag(langTag);
                }
            }
        }
        return cachedLocale;
    }

    public String get(String key) {
        return cache.computeIfAbsent(key, k -> {
            try {
                String value = configManager.getLanguageConfig().getString(k);
                return (value != null) ? value : k;
            } catch (Exception e) {
                log.error("Error while loading language file: {}", e.getMessage());
                return k;
            }
        });
    }

    public String format(String key, Object... args) {
        String message = get(key);
        if (args == null || args.length == 0) {
            return message;
        }

        if (args.length % 2 != 0) {
            log.warn("Varargs passed to Lang.format for key '{}' are not balanced pairs!", key);
            return message;
        }

        for (int i = 0; i < args.length; i += 2) {
            String placeholder = "{" + args[i] + "}";
            String value = String.valueOf(args[i + 1]);
            message = message.replace(placeholder, value);
        }

        return message;
    }

    public String formatPeriod(Period period) {
        List<String> parts = new ArrayList<>();
        addPart(parts, period.getYears(), Time.YEAR, Time.YEARS);
        addPart(parts, period.getMonths(), Time.MONTH, Time.MONTHS);
        addPart(parts, period.getDays(), Time.DAY, Time.DAYS);

        return parts.isEmpty() ? format(Time.DAYS, "count", 0) : String.join(", ", parts);
    }

    public String formatDuration(Duration duration) {
        List<String> parts = new ArrayList<>();
        addPart(parts, duration.toHoursPart(), Time.HOUR, Time.HOURS);
        addPart(parts, duration.toMinutesPart(), Time.MINUTE, Time.MINUTES);

        return parts.isEmpty() ? " " : String.join(" ", parts);
    }

    private void addPart(List<String> parts, int count, String singularKey, String pluralKey) {
        if (count > 0) {
            parts.add(format(count == 1 ? singularKey : pluralKey, "count", count));
        }
    }
}
