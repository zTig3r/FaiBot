package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.ztiger.faibot.FaiBot.cfgm;
import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.listeners.BotReady.GUILD;

public class Colors {

    public static HashMap<String, ColorInfo> colors = new HashMap<>();

    public static final Color nixo = new Color(0x94c6f3);

    private static List<Button> colorButtons = new ArrayList<>();

    public static void setupColors() {
        try {
            FileConfiguration colorFile = cfgm.getConfig("colors");

            colorFile.getKeys(false).forEach(color -> colors.put(color, new ColorInfo(colorFile.getString(color + ".translation"), colorFile.getString(color + ".emoji"), colorFile.getString(color + ".hex"))));

            colorButtons = colors.entrySet().stream().map(entry -> Button.secondary(entry.getKey(), entry.getValue().emoji + " " + entry.getValue().translation)).collect(Collectors.toList());

            colors.values().forEach(info -> {
                List<Role> roles = GUILD.getRolesByName(info.translation, true);
                int colorRGB = Color.decode(info.hex).getRGB();

                if (roles.isEmpty()) GUILD.createRole().setName(info.translation).setColor(colorRGB).queue();
                else if (roles.get(0).getColorRaw() != colorRGB) roles.get(0).getManager().setColor(colorRGB).queue();
            });
        } catch (Exception e) {
            logger.error("Error while loading colors file: {}", e.getMessage());
        }
    }

    public static class ColorInfo {
        public String translation;
        public String emoji;
        public String hex;

        public ColorInfo(String translation, String emoji, String hex) {
            this.translation = translation;
            this.emoji = emoji;
            this.hex = hex;
        }
    }

    public static List<ActionRow> createColorActionRows() {
        return IntStream.range(0, (colorButtons.size() + 4) / 5)
                .mapToObj(i -> ActionRow.of(colorButtons.subList(i * 5, Math.min((i + 1) * 5, colorButtons.size()))))
                .collect(Collectors.toList());
    }

    public static List<String> getTranslations() {
        return colors.values().stream().map(colorInfo -> colorInfo.translation).collect(Collectors.toList());
    }
}
