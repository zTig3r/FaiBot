package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.cfgm;
import static de.ztiger.faibot.utils.Colors.nixo;

public class EmbedCreator {

    private static final HashMap<String, MessageEmbed> embeds = new HashMap<>();

    private static final String EMPTYLINE = "\u00A0";
    private static final Field EMPTY = new MessageEmbed.Field("", EMPTYLINE, false, false);

    public static MessageEmbed getEmbed(String type) {
        return embeds.computeIfAbsent(type, s -> createEmbed(s, null, nixo));
    }

    public static MessageEmbed getEmbed(String type, Map<String, String> replacements) {
        return createEmbed(type, replacements, nixo);
    }

    public static MessageEmbed getEmbed(String type, Map<String, String> replacements, Color color) {
        return createEmbed(type, replacements, color);
    }

    private static MessageEmbed createEmbed(String type, Map<String, String> replacements, Color color) {
        LinkedList<String> values = new LinkedList<>(cfgm.getConfig("embeds").getStringList(type.split("_")[0]));
        EmbedBuilder builder = new EmbedBuilder();

        if (replacements != null) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                values.replaceAll(s -> s.replace("{" + entry.getKey() + "}", entry.getValue()));

                if (entry.getKey().startsWith("field")) values.add(values.size() - 1, "n:" + entry.getValue());
            }

            if (replacements.keySet().stream().anyMatch(key -> key.startsWith("author")))
                builder.setAuthor(replacements.get("author_name"), null, replacements.get("author_icon"));
        }

        if (!values.get(0).isEmpty()) builder.setTitle(values.get(0));
        builder.setColor(color);

        for (String content : values.subList(1, values.size())) {
            String value = content.split(":").length > 1 ? content.split(":", 2)[1] : content;

            switch (content.split(":")[0]) {
                case "d" -> builder.setDescription(value);
                case "b" -> builder.addField(value, EMPTYLINE, false);
                case "n" -> builder.addField(EMPTYLINE, value, false);
                case "ts" -> builder.setTimestamp(OffsetDateTime.now());
                case "tn" -> builder.setThumbnail(value);
                case "bl" -> {
                    String[] split = value.split(";");
                    builder.addField(split[0], split[1], false);
                }
                default -> {
                    if (content.isEmpty()) builder.addField(EMPTY);
                    else builder.setFooter(content);
                }
            }
        }

        return builder.build();
    }
}
