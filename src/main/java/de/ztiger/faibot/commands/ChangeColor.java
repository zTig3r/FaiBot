package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.*;

import java.awt.*;
import java.util.*;
import java.util.List;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.commands.Stats.sendPreview;

@SuppressWarnings("ConstantConditions")
public class ChangeColor {

    private static final Button red = Button.secondary("red", "🟥 Rot");
    private static final Button blue = Button.secondary("blue", "🟦 Blau");
    private static final Button pink = Button.secondary("pink", "🟪 Pink");
    private static final Button green = Button.secondary("green", "🟩 Grün");
    private static final Button orange = Button.secondary("orange", "🟧 Orange");
    private static final Button purple = Button.secondary("purple", "🟪 Violett");
    private static final Button lightblue = Button.secondary("lightblue", "🟦 Hellblau");
    private static final Button yellow = Button.secondary("yellow", "🟨 Gelb");
    private static final Button reset = Button.danger("reset", "🗑️ Farbe entfernen");
    private static final Button back = Button.danger("back", "↩️ Zurück");
    private static final HashMap<Member, String> colorCache = new HashMap<>();

    public static void sendColorEmbed(SlashCommandInteractionEvent event) {
        event.reply((MessageCreateData) getColorEmbed('n')).setEphemeral(true).queue();
    }

    public static void sendColorEmbed(ButtonInteractionEvent event) {
        event.editMessage((MessageEditData) getColorEmbed('e')).queue();
    }

    public static void nameColorEmbed(ButtonInteractionEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("🎨 Farbe für deinen Namen ändern")
                .setDescription("Hier kannst du deine Farbe für deinen Namen ändern.")
                .addField("", "\u00A0", false)
                .addField("⚠️ Du kannst nur Farben auswählen, welche du bereits freigeschalten hast.", "", false)
                .addField("➡️ Wähle aus, welche Farbe du für deinen Namen verwenden möchtest.", "\u00A0", false)
                .setColor(Color.decode("#94c6f3"))
                .build();

        event.editMessageEmbeds(embed).setComponents(ActionRow.of(orange, blue, yellow, pink, reset), ActionRow.of(purple, red, green, lightblue, back)).queue();
    }

    public static void statsColorEmbed(ButtonInteractionEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("🎨 Farbe für deine Statistiken ändern")
                .setDescription("Hier kannst du deine Farbe für deine Statistiken ändern.")
                .addField("", "\u00A0", false)
                .addField("⚠️ Du kannst nur Farben auswählen, welche du bereits freigeschalten hast.", "", false)
                .addField("➡️ Wähle aus, welche Farbe du für deine Statistiken verwenden möchtest.", "\u00A0", false)
                .setColor(Color.decode("#94c6f3"))
                .build();

        event.editMessageEmbeds(embed).setComponents(ActionRow.of(red, blue, pink, green, reset), ActionRow.of(orange, purple, lightblue, yellow, back)).setAttachments().queue();
    }


    private static MessageData getColorEmbed(char type) {
        Button nameColor = Button.primary("nameColor", "👋 Name");
        Button statsColor = Button.primary("statsColor", "📊 Statistiken");

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("🎨 Farben ändern")
                .setDescription("Hier kannst du deine Farbe für deinen Namen und deine Statistiken ändern.")
                .addField("", "\u00A0", false)
                .addField("➡️ Wähle zuerst aus welche Farbe du ändern möchtest.", "\u00A0", false)
                .setColor(Color.decode("#94c6f3"))
                .build();

        if (type == 'n')
            return new MessageCreateBuilder().setEmbeds(embed).setActionRow(nameColor, statsColor).build();
        else if (type == 'e')
            return new MessageEditBuilder().setEmbeds(embed).setActionRow(nameColor, statsColor).build();

        return null;
    }

    public static void changeColor(ButtonInteractionEvent event) {
        List<String> colors = new ArrayList<>(getter.getInventory(event.getMember().getId()));

        Map<String, Runnable> colorActions = Map.of(
                "red", () -> handleColor("red", event),
                "blue", () -> handleColor("blue", event),
                "pink", () -> handleColor("pink", event),
                "green", () -> handleColor("green", event),
                "orange", () -> handleColor("orange", event),
                "lightblue", () -> handleColor("lightblue", event),
                "yellow", () -> handleColor("yellow", event),
                "purple", () -> handleColor("purple", event),
                "reset", () -> handleReset(event)
        );

        String buttonId = event.getButton().getId();
        if (colors.contains(buttonId) || buttonId.equals("reset")) colorActions.get(buttonId).run();
        else event.reply("Du besitzt diese Farbe nicht! *Kaufe weitere Farben:* `/shop`").setEphemeral(true).queue();
    }

    private static void handleReset(ButtonInteractionEvent event) {
        String title = event.getMessage().getEmbeds().get(0).getTitle();
        Member member = event.getMember();

        if (title.contains("Name")) {
            member.getRoles().stream()
                    .filter(role -> Arrays.asList("Rot", "Blau", "Pink", "Grün", "Orange", "Violett", "Hellblau", "Gelb").contains(role.getName()))
                    .forEach(role -> event.getGuild().removeRoleFromMember(member, role).queue());
            logger.info("Resetting namecolor for " + member.getEffectiveName());
            event.editMessage("Deine Namenfarbe wurde erfolgreich zurückgesetzt!").setEmbeds().setAttachments().setComponents().queue();
        } else if (title.contains("Statistiken")) {
            logger.info("Resetting statscolor for " + member.getEffectiveName());
            setter.setCardColor(member.getId(), "#94c6f3");
            event.editMessage("Deine Statistikfarbe wurde erfolgreich zurückgesetzt!").setEmbeds().setAttachments().setComponents().queue();
        }
    }

    private static void handleColor(String color, ButtonInteractionEvent event) {
        String title = event.getMessage().getEmbeds().get(0).getTitle();
        Member member = event.getMember();

        if (title.contains("Name")) setNameColor(event, color);
        else if (title.contains("Statistiken")) {
            colorCache.put(member, color);
            sendPreview(event, color);
        }
    }

    public static void applyColor(ButtonInteractionEvent event) {
        Member member = event.getMember();
        setter.setCardColor(member.getId(), colorCache.get(member));

        logger.info("Setting statscolor for " + member.getEffectiveName() + " to " + colorCache.get(member));
        event.editMessage("Deine Statistikfarbe wurde erfolgreich zu ` " + colorCache.get(member) + " ` geändert!").setAttachments().setComponents().queue();

        colorCache.remove(member);
    }


    private static void setNameColor(ButtonInteractionEvent event, String color) {
        Member member = event.getMember();
        member.getRoles().stream()
                .filter(role -> Arrays.asList("Rot", "Blau", "Pink", "Grün", "Orange", "Violett", "Hellblau", "Gelb").contains(role.getName()))
                .forEach(role -> event.getGuild().removeRoleFromMember(member, role).queue());

        Map<String, String> colorToRole = Map.of(
                "red", "Rot",
                "blue", "Blau",
                "pink", "Pink",
                "green", "Grün",
                "orange", "Orange",
                "purple", "Violett",
                "lightblue", "Hellblau",
                "yellow", "Gelb"
        );

        String newRole = colorToRole.getOrDefault(color, null);
        if (newRole != null) {
            event.getGuild().addRoleToMember(member, event.getGuild().getRolesByName(newRole, true).get(0)).queue();
        }

        logger.info("Setting namecolor for " + member.getEffectiveName() + " to " + color);
        event.editMessage("Deine Namenfarbe wurde erfolgreich zu ` " + color + " ` geändert!").setEmbeds().setComponents().queue();
    }
}
