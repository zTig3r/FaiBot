package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.ztiger.faibot.FaiBot.*;

@SuppressWarnings("ConstantConditions")
public class Shop {

    private static final Button red = Button.secondary("BUYred", "🟥 Rot");
    private static final Button blue = Button.secondary("BUYblue", "🟦 Blau");
    private static final Button pink = Button.secondary("BUYpink", "🟪 Pink");
    private static final Button green = Button.secondary("BUYgreen", "🟩 Grün");
    private static final Button orange = Button.secondary("BUYorange", "🟧 Orange");
    private static final Button purple = Button.secondary("BUYpurple", "🟪 Violett");
    private static final Button lightblue = Button.secondary("BUYlightblue", "🟦 Hellblau");
    private static final Button yellow = Button.secondary("BUYyellow", "🟨 Gelb");
    private static final Button cancel = Button.danger("BUYcancel", "✕ Abbrechen");
    private static final Button confirm = Button.success("BUYconfirm", "✓ Bestätigen");
    private static final HashMap<Member, String> shopCache = new HashMap<>();

    public static void sendShopEmbed(SlashCommandInteractionEvent event) {
        List<String> colors = new ArrayList<>(getter.getInventory(event.getMember().getId()));

        event.replyEmbeds(createShopEmbed()).setComponents(getActionRows(colors)).setEphemeral(true).queue();
    }

    public static void sendShopEmbed(ButtonInteractionEvent event) {
        List<String> colors = new ArrayList<>(getter.getInventory(event.getMember().getId()));

        event.editMessageEmbeds(createShopEmbed()).setComponents(getActionRows(colors)).queue();
    }

    public static List<ActionRow> getActionRows(List<String> colors) {
        List<Button> buttons = new ArrayList<>();

        for (Button button : new Button[]{orange, blue, yellow, pink, purple, red, green, lightblue}) {
            if (colors.contains(button.getId().replace("BUY", ""))) buttons.add(button.asDisabled());
            else buttons.add(button);
        }

        return List.of(ActionRow.of(buttons.subList(0, 4)), ActionRow.of(buttons.subList(4, 8)));
    }

    private static MessageEmbed createShopEmbed() {
        return new EmbedBuilder()
                .setTitle("🛒 Shop")
                .addField("\u00A0", "Hier kannst du dir Farben für deinen Namen und deine Statistiken kaufen.", false)
                .addField("Jede Farbe kostet 750 Punkte.", "\u00A0", false)
                .addField("➡️ Wähle zuerst aus welche Farbe du kaufen möchtest", "\u00A0", false)
                .setColor(Color.decode("#94c6f3"))
                .build();
    }

    public static void handleShopEmbed(ButtonInteractionEvent event) {
        String color = " ";

        switch (event.getButton().getId().substring(3)) {
            case "red" -> color = "Rot";
            case "blue" -> color = "Blau";
            case "green" -> color = "Grün";
            case "pink" -> color = "Rosa";
            case "orange" -> color = "Orange";
            case "purple" -> color = "Violett";
            case "lightblue" -> color = "Hellblau";
            case "yellow" -> color = "Gelb";
        }

        shopCache.put(event.getMember(), event.getButton().getId().substring(3));

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("🛒 Shop")
                .setDescription("Bist du dir sicher, dass du dir die Farbe **" + color + "** für **750 Points** kaufen möchtest?")
                .setColor(Color.decode("#94c6f3"))
                .build();

        ActionRow row = ActionRow.of(confirm, cancel);

        event.editMessageEmbeds(embed).setComponents(row).queue();
    }

    public static void handleBuy(ButtonInteractionEvent event) {
        if (getter.getPoints(event.getMember().getId()) < 750) {
            event.reply("Du hast nicht genug Punkte um dir diese Farbe zu kaufen!").setEphemeral(true).queue();
            return;
        }

        String color = " ";

        switch (shopCache.get(event.getMember())) {
            case "red" -> color = "Rot";
            case "blue" -> color = "Blau";
            case "green" -> color = "Grün";
            case "pink" -> color = "Rosa";
            case "orange" -> color = "Orange";
            case "purple" -> color = "Violett";
            case "lightblue" -> color = "Hellblau";
            case "yellow" -> color = "Gelb";
        }

        setter.removePoints(event.getMember().getId(), 750);
        setter.addInventory(event.getMember().getId(), shopCache.get(event.getMember()));

        shopCache.remove(event.getMember());

        logger.info("User " + event.getMember().getUser().getAsTag() + " bought the color " + color);
        event.editMessage("Du hast dir erfolgreich die Farbe **" + color + "** gekauft!").setComponents().setEmbeds().queue();
    }
}
