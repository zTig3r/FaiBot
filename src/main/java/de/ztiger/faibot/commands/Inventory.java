package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static de.ztiger.faibot.FaiBot.getter;

@SuppressWarnings("ConstantConditions")
public class Inventory {

    public static void sendInventory(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl())
                .setTitle("Inventar")
                .setColor(Color.decode("#94c6f3"));

        List<String> colors = new ArrayList<>(getter.getInventory(event.getMember().getId()));

        if(colors.isEmpty()) {
            embed.addField("\u00A0", "Du besitzt derzeit keine Items", false);
        }

        for (String color : colors) {
            String converted = "";

            switch(color) {
                case "red" -> converted = "Rote Farbe";
                case "blue" -> converted = "Blaue Farbe";
                case "green" -> converted = "Grüne Farbe";
                case "pink" -> converted = "Rosa Farbe";
                case "orange" -> converted = "Orange Farbe";
                case "purple" -> converted = "Violette Farbe";
                case "lightblue" -> converted = "Hellblaue Farbe";
                case "yellow" -> converted = "Gelbe Farbe";
            }

            embed.addField("\u00A0", converted, false);
        }

        embed.setFooter("Weitere Items freischalten: /shop");

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();

    }
}
