package de.ztiger.faibot.utils;

import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    @SuppressWarnings("ConstantConditions, ResultOfMethodCallIgnored")
    private void setup() {
        configList.forEach(filename -> {
            try {
                if(!new File("./configs").exists()) new File("./configs").mkdir();

                File configFile = new File("./configs/" + filename + ".yml");
                if(!configFile.exists()) Files.copy(ConfigManager.class.getClassLoader().getResourceAsStream(filename + ".yml"), configFile.toPath());

                configs.put(filename, YamlConfiguration.loadConfiguration(configFile));
            } catch (Exception e) {
                logger.error("Error while loading config file {}: {}", filename, e.getMessage());
            }
        });
    }

    public FileConfiguration getConfig(String name) {
        return configs.get(name);
    }

    public void saveConfig(String filename) {
        try {
            configs.get(filename).save(new File("./configs/", filename + ".yml"));
        } catch (IOException e) {
            logger.error("Error while saving config file {}: {}", filename, e.getMessage());
        }
    }
}
