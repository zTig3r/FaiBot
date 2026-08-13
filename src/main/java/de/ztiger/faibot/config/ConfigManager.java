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
import java.util.*;

@Slf4j
public class ConfigManager {

    private final Dotenv env;

    @Getter
    private FileConfiguration config = new YamlConfiguration();

    private final Map<String, FileConfiguration> langConfigs = new HashMap<>();
    private final Path configDir;

    public ConfigManager(Dotenv env) {
        this.env = env;
        this.configDir = resolveConfigDirectory();
        log.info("Using configuration directory: {}", configDir.toAbsolutePath());
        config = loadYamlFile("config");
    }

    private Path resolveConfigDirectory() {
        String configPath = env.get("CONFIG_PATH");
        if (configPath == null || configPath.isBlank()) throw new RuntimeException("Config path is not defined.");
        return Paths.get(configPath);
    }

    public FileConfiguration getLanguageConfig(String langCode) {
        return langConfigs.computeIfAbsent(langCode, this::loadYamlFile);
    }

    private FileConfiguration loadYamlFile(String fileName) {
        try {
            File baseFile = configDir.resolve(fileName + ".yml").toFile();
            File localFile = configDir.resolve(fileName + ".local.yml").toFile();
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