package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.entities.Role;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.config.ConfigHelper.getColorsConfig;

public class Colors {

    public static HashMap<String, ColorInfo> colors = new HashMap<>();

    public static final Color nixo = new Color(0x94c6f3);

    private static List<SelectOption> colorOptions = new ArrayList<>();

    public static void setupColors() {
        try {
            FileConfiguration colorFile = getColorsConfig();

            colorFile.getKeys(false).forEach(color -> colors.put(color, new ColorInfo(colorFile.getString(color + ".translation"), colorFile.getString(color + ".emoji"), colorFile.getString(color + ".hex"))));

            colorOptions = colors.entrySet().stream().map(entry -> SelectOption.of(entry.getValue().emoji + " " + entry.getValue().translation, entry.getKey())).collect(Collectors.toList());

            GuildProvider.getMainGuild().ifPresent(guild -> colors.values().forEach(info -> {
                List<Role> roles = guild.getRolesByName(info.translation, true);
                int colorRGB = Color.decode(info.hex).getRGB();

                if (roles.isEmpty()) guild.createRole().setName(info.translation).setColor(colorRGB).queue();
                else if (roles.getFirst().getColorRaw() != colorRGB)
                    roles.getFirst().getManager().setColor(colorRGB).queue();
            }));

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

    public static List<SelectOption> getColorOptions(String ID, List<String> items) {

        return colorOptions.stream().filter(option -> (ID.equals("BUY") == items.contains(option.getValue()))).collect(Collectors.toList());



       /* return IntStream.range(0, (colorButtons.size() + 4) / 5)
                .mapToObj(i -> ActionRow.of(colorButtons.subList(i * 5, Math.min(i * 5 + 5, colorButtons.size())).stream()
                        .map(button -> (ID.equals("BUY") == items.contains(button.getCustomId()))
                                ? button.asDisabled()
                                : button.withId(ID + button.getCustomId())).collect(Collectors.toList()))).collect(Collectors.toList());
*/
    }

    public static List<String> getTranslations() {
        return colors.values().stream().map(colorInfo -> colorInfo.translation).collect(Collectors.toList());
    }

    public static Color convertColor(String color) {
        return Color.decode((color.contains("#") ? "#94c6f3" : colors.get(color).hex));
    }
}
