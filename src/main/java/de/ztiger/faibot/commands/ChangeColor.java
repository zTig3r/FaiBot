package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.commands.Stats.sendPreview;
import static de.ztiger.faibot.listeners.BotReady.GUILD;
import static de.ztiger.faibot.utils.Colors.*;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

@SuppressWarnings("ConstantConditions")
public class ChangeColor {

    private static final String KEY = "color.";

    private static final List<Button> colorButtons = createColorButtons();
    private static final Button reset = Button.danger("reset", getLang(KEY + "reset"));
    private static final Button back = Button.danger("back", "↩️ Zurück");
    private static final HashMap<Member, String> colorCache = new HashMap<>();

    public static void sendColorEmbed(SlashCommandInteractionEvent event) {
        event.reply((MessageCreateData) getColorEmbed('n')).setEphemeral(true).queue();
    }

    public static void sendColorEmbed(ButtonInteractionEvent event) {
        event.editMessage((MessageEditData) getColorEmbed('e')).queue();
    }

    public static void colorEmebd(ButtonInteractionEvent event, boolean isName) {
        Map<String, Object> replacement = Map.of("type", isName ? getLang("color.type.name") : getLang("color.type.stats"));

        String key = KEY + "select.";

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(format(key + "title", replacement))
                .setDescription(format(key + "description", replacement))
                .addField("", "\u00A0", false)
                .addField(getLang(key + "warn"), "", false)
                .addField(format(key + "task", replacement), "\u00A0", false)
                .setColor(nixo)
                .build();

        event.editMessageEmbeds(embed).setComponents(ActionRow.of(colorButtons.get(0), colorButtons.get(1), colorButtons.get(2), colorButtons.get(3), reset), ActionRow.of(colorButtons.get(4), colorButtons.get(5), colorButtons.get(6), colorButtons.get(7), back)).queue();
    }

    private static MessageData getColorEmbed(char type) {
        Button nameColor = Button.primary("nameColor", "👋 Name");
        Button statsColor = Button.primary("statsColor", "📊 Statistiken");

        String key = "color.main.";

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(getLang(key + "title"))
                .setDescription(getLang(key + "description"))
                .addField("", "\u00A0", false)
                .addField(getLang(key + "task"), "\u00A0", false)
                .setColor(nixo)
                .build();

        if (type == 'n')
            return new MessageCreateBuilder().setEmbeds(embed).setActionRow(nameColor, statsColor).build();
        else if (type == 'e')
            return new MessageEditBuilder().setEmbeds(embed).setActionRow(nameColor, statsColor).build();

        return null;
    }

    public static void changeColor(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();

        if (buttonId.equals("reset")) handleReset(event);
        else if (getter.getInventory(event.getMember().getId()).contains(buttonId)) handleColor(event);
        else event.reply(getLang("colors.locked")).setEphemeral(true).queue();
    }

    private static void handleReset(ButtonInteractionEvent event) {
        String title = event.getMessage().getEmbeds().get(0).getTitle();
        Member member = event.getMember();

        if (title.contains("Name")) {
            resetNameColor(member);
            logger.info("Resetting namecolor for " + member.getEffectiveName());
        } else if (title.contains("Statistiken")) {
            setter.setCardColor(member.getId(), "#94c6f3");
            logger.info("Resetting statscolor for " + member.getEffectiveName());
        }

        String key = "color.type.";

        event.editMessage(format("color.reset", Map.of("type", title.contains("Name") ? getLang(key + "name") : getLang(key + "stats")))).setEmbeds().setAttachments().setComponents().queue();
    }

    private static void handleColor(ButtonInteractionEvent event) {
        String title = event.getMessage().getEmbeds().get(0).getTitle();
        String color = event.getButton().getId();

        if (title.contains("Name")) setNameColor(event, color);
        else if (title.contains("Statistiken")) {
            colorCache.put(event.getMember(), color);
            sendPreview(event, color);
        }
    }

    public static void applyColor(ButtonInteractionEvent event) {
        Member member = event.getMember();
        setter.setCardColor(member.getId(), colorCache.get(member));

        logger.info("Setting statscolor for " + member.getEffectiveName() + " to " + colorCache.get(member));
        event.editMessage("Deine Statistikfarbe wurde erfolgreich zu ` " + colors.get(colorCache.get(member)).translation + " ` geändert!").setAttachments().setComponents().queue();

        colorCache.remove(member);
    }


    private static void setNameColor(ButtonInteractionEvent event, String color) {
        Member member = event.getMember();

        resetNameColor(member);

        String newRole = colors.get(color).translation;

        event.getGuild().addRoleToMember(member, event.getGuild().getRolesByName(newRole, true).get(0)).queue();

        logger.info("Setting namecolor for " + member.getUser().getName() + " to " + color);
        event.editMessage("Deine Namensfarbe wurde erfolgreich zu ` " + newRole + " ` geändert!").setEmbeds().setComponents().queue();
    }

    private static void resetNameColor(Member member) {
        List<String> translations = getTranslations();

        member.getRoles().stream()
                .filter(role -> translations.contains(role.getName()))
                .forEach(role -> GUILD.removeRoleFromMember(member, role).queue());
    }
}
