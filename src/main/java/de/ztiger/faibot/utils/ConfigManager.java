package de.ztiger.faibot.utils;

import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.logger;

public class ConfigManager {

    private final Map<String, FileConfiguration> configs = new HashMap<>();

    private static final List<String> configList = Arrays.asList("colors", "config", "de_DE", "embeds");

    public ConfigManager() {
        setup();
    }

    @SuppressWarnings("ConstantConditions")
    private void setup() {
        configList.forEach(filename -> {
            try {
                configs.put(filename, YamlConfiguration.loadConfiguration(new File(ConfigManager.class.getClassLoader().getResource(filename + ".yml").toURI())));
            } catch (Exception e) {
                logger.error("Error while loading config file {}: {}", filename, e.getMessage());
            }
        });
    }

    public FileConfiguration getConfig(String name) {
        return configs.get(name);
    }
}
