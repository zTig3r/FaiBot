package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Colors {

    public static final Color nixo = new Color(0x94c6f3);

    public static final HashMap<String, ColorInfo> colors = new HashMap<>() {{
        put("red", new ColorInfo("Rot", "🟥", "#a30000"));
        put("blue", new ColorInfo("Blau", "🟦", "#206694"));
        put("pink", new ColorInfo("Pink", "🟪", "#ff00b0"));
        put("green", new ColorInfo("Grün", "🟩", "#1abc9c"));
        put("orange", new ColorInfo("Orange", "🟧", "#e67e22"));
        put("purple", new ColorInfo("Violett", "🟪", "#7f00b4"));
        put("lightblue", new ColorInfo("Hellblau", "🟦", "#00f1ff"));
        put("yellow", new ColorInfo("Gelb", "🟨", "#faff00"));
    }};

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

    public static List<Button> createColorButtons() {
        List<Button> buttons = new ArrayList<>();

        for(String color : colors.keySet()) {
            ColorInfo colorInfo = colors.get(color);
            buttons.add(Button.secondary(color, colorInfo.emoji + " " + colorInfo.translation));
        }

        return buttons;
    }

    public static List<String> getTranslations() {
        return colors.values().stream().map(colorInfo -> colorInfo.translation).collect(Collectors.toList());
    }
}
