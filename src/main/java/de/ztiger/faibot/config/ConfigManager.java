package de.ztiger.faibot.config;

import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.ztiger.faibot.FaiBot.env;
import static de.ztiger.faibot.FaiBot.logger;

public class ConfigManager {

    private final Map<ConfigType, FileConfiguration> configs = new EnumMap<>(ConfigType.class);
    private final Map<String, FileConfiguration> langConfigs = new HashMap<>();
    private final Path configDir;

    public ConfigManager() {
        this.configDir = resolveConfigDirectory();
        logger.info("Using configuration directory: {}", configDir.toAbsolutePath());
        setup();
    }

    private Path resolveConfigDirectory() {
        String configPath = env.get("CONFIG_PATH");
        if (configPath == null || configPath.isBlank()) throw new RuntimeException("Config path is not defined.");
        return Paths.get(configPath);
    }

    private void setup() {
        for (ConfigType type : ConfigType.values()) {
            configs.put(type, loadYamlFile(type.getFileName()));
        }
    }

    public FileConfiguration getConfig(ConfigType type) {
        return configs.get(type);
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
                logger.warn("Config file {} does not exist.", configFile.getName());
                return new YamlConfiguration();
            }

            logger.info("Loading config: {}", configFile.getName());
            return YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            logger.error("Error loading config file {}: {}", fileName, e.getMessage(), e);
            return new YamlConfiguration();
        }
    }
}