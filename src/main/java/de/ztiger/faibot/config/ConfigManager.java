package de.ztiger.faibot.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ConfigManager {

    private final Dotenv env;
    private final Map<String, FileConfiguration> langConfigs = new HashMap<>();
    private final Path langDir;

    @Getter
    private FileConfiguration config = new YamlConfiguration();

    public ConfigManager(Dotenv env) {
        this.env = env;
        Path configDir = resolveDirectory("CONFIG_PATH", "Config");
        this.langDir = resolveDirectory("LOCALIZATION_PATH", "Localization");
        log.info("Using configuration directory: {}", configDir.toAbsolutePath());
        log.info("Using localization directory: {}", langDir.toAbsolutePath());
        config = loadYamlFile("config", configDir);
    }

    private Path resolveDirectory(String envVar, String description) {
        String path = env.get(envVar);
        if (path == null || path.isBlank()) {
            throw new RuntimeException(description + " path is not defined.");
        }
        return Paths.get(path);
    }

    public FileConfiguration getLanguageConfig(String langCode) {
        return langConfigs.computeIfAbsent(langCode, lang -> loadYamlFile(lang, langDir));
    }

    private FileConfiguration loadYamlFile(String fileName, Path directory) {
        try {
            File baseFile = directory.resolve(fileName + ".yml").toFile();
            File localFile = directory.resolve(fileName + ".local.yml").toFile();
            File configFile = localFile.exists() ? localFile : baseFile;

            if (!configFile.exists()) {
                log.warn("Config file {} does not exist.", configFile.getName());
                return new YamlConfiguration();
            }

            log.info("Loading config: {}", configFile.getName());
            return YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            log.error("Error loading config file {}: {}", fileName, e.getMessage(), e);
            return new YamlConfiguration();
        }
    }

    public String getLanguage() {
        return config.getString("language", "de_DE");
    }

    public FileConfiguration getLanguageConfig() {
        return getLanguageConfig(getLanguage());
    }

    public String getGuildId() {
        return config.getString("guild");
    }

    public String getChannelId(String key) {
        return config.getString("channel." + key);
    }

    public String getRoleId(String key) {
        return config.getString("role." + key);
    }

    public Color getColor(BotColor color) {
        String hex = config.getString("color." + color.getConfigKey());
        if (hex == null || hex.isBlank()) {
            log.warn("Color key 'color.{}' missing in config. Defaulting to white.", color.getConfigKey());
            return Color.WHITE;
        }
        try {
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            log.error("Invalid color format for key 'color.{}': {}", color.getConfigKey(), hex);
            return Color.WHITE;
        }
    }
}