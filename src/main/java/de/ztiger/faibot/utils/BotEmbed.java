package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.OffsetDateTime;

public class BotEmbed {

    private static final String ZERO_WIDTH_SPACE = "\u00A0";
    private final EmbedBuilder builder = new EmbedBuilder();

    private BotEmbed(Color color) {
        this.builder.setColor(color);
    }

    // TODO: Implement customs color support via config

    public static BotEmbed defaultEmbed() {
        return new BotEmbed(Colors.nixo);
    }

    public static BotEmbed success() {
        return new BotEmbed(Color.GREEN);
    }

    public static BotEmbed warning() {
        return new BotEmbed(Color.YELLOW);
    }

    public static BotEmbed error() {
        return new BotEmbed(Color.RED);
    }

    public BotEmbed title(String title) {
        if (title != null && !title.isBlank()) builder.setTitle(title);
        return this;
    }

    public BotEmbed description(String description) {
        if (description != null && !description.isBlank()) builder.setDescription(description);
        return this;
    }

    public BotEmbed thumbnail(String url) {
        if (url != null && !url.isBlank()) builder.setThumbnail(url);
        return this;
    }

    public BotEmbed withTimestamp() {
        builder.setTimestamp(OffsetDateTime.now());
        return this;
    }

    public BotEmbed footer(String text) {
        if (text != null && !text.isBlank()) builder.setFooter(text);
        return this;
    }

    public BotEmbed footer(String text, String iconUrl) {
        if (text != null && !text.isBlank()) builder.setFooter(text, iconUrl);
        return this;
    }

    public BotEmbed author(String name, String iconUrl) {
        if (name != null && !name.isBlank()) builder.setAuthor(name, null, iconUrl);
        return this;
    }

    public BotEmbed field(String name, String value) {
        return field(name, value, false);
    }

    public BotEmbed field(String name, String value, boolean inline) {
        builder.addField(name, value, inline);
        return this;
    }

    public BotEmbed boldField(String title) {
        builder.addField(title, ZERO_WIDTH_SPACE, false);
        return this;
    }

    public BotEmbed normalField(String value) {
        builder.addField(ZERO_WIDTH_SPACE, value, false);
        return this;
    }

    public BotEmbed emptyLine() {
        builder.addField("", ZERO_WIDTH_SPACE, false);
        return this;
    }

    public MessageEmbed build() {
        return builder.build();
    }
}