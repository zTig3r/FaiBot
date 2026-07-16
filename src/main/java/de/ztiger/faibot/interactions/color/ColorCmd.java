package de.ztiger.faibot.interactions.color;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IButtonHandler;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.interactions.stats.StatsCmd.sendPreview;
import static de.ztiger.faibot.listeners.BotReady.GUILD;
import static de.ztiger.faibot.utils.Colors.*;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

public class ColorCmd implements ICommand, IButtonHandler {

    private static final String PREFIX = "color";
    private static final String KEY = "color.";

    private static final String ACTION_BACK = "back";
    private static final String ACTION_MENU_NAME = "menu_name";
    private static final String ACTION_MENU_STATS = "menu_stats";
    private static final String ACTION_RESET = "reset";
    private static final String ACTION_CONFIRM = "confirm";

    @Override
    public CommandData getCommandData() {
        return Commands.slash("color", "Ändere deine Farben");
    }

    @Override
    public String getComponentPrefix() {
        return PREFIX;
    }

    private static String buildId(String action, String payload) {
        return payload == null || payload.isEmpty()
                ? PREFIX + ":" + action
                : PREFIX + ":" + action + ":" + payload;
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        Button nameColorBtn = Button.primary(buildId(ACTION_MENU_NAME, null), getLang(KEY + "name"));
        Button statsColorBtn = Button.primary(buildId(ACTION_MENU_STATS, null), getLang(KEY + "stats"));

        event.replyComponents(ActionRow.of(nameColorBtn, statsColorBtn))
                .setEmbeds(getEmbed("changeColorMenu"))
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void handleButton(ButtonInteractionEvent event, String action, String payload) {
        switch (action) {
            case ACTION_BACK       -> sendMainColorMenu(event);
            case ACTION_MENU_NAME  -> sendSubMenuEmbed(event, true);
            case ACTION_MENU_STATS -> sendSubMenuEmbed(event, false);
            case ACTION_RESET      -> handleReset(event, payload);
            case ACTION_CONFIRM    -> applyStatsColor(event, payload);
            default -> logger.warn("Unknown color button action: {}", action);
        }
    }

    public static void sendMainColorMenu(ButtonInteractionEvent event) {
        Button nameColorBtn = Button.primary(buildId(ACTION_MENU_NAME, null), getLang(KEY + "name"));
        Button statsColorBtn = Button.primary(buildId(ACTION_MENU_STATS, null), getLang(KEY + "stats"));

        event.editComponents(ActionRow.of(nameColorBtn, statsColorBtn))
                .setEmbeds(getEmbed("changeColorMenu"))
                .queue();
    }

    public static void sendSubMenuEmbed(ButtonInteractionEvent event, boolean isName) {
        String typeStr = isName ? "NAME" : "STATS";
        List<ActionRow> rows = new ArrayList<>();

        Button reset = Button.danger(buildId(ACTION_RESET, typeStr), getLang(KEY + "reset"));
        Button back = Button.secondary(buildId(ACTION_BACK, null), getLang(KEY + "back"));

        String selectMenuId = PREFIX + ":select:" + typeStr;

        rows.add(ActionRow.of(StringSelectMenu.create(selectMenuId)
                .addOptions(getColorOptions(typeStr, getter.getInventory(event.getMember().getId())))
                .build()));
        rows.add(ActionRow.of(reset, back));

        String displayType = isName ? getLang("color.type.name") : getLang("color.type.stats");
        event.editMessageEmbeds(getEmbed("changeColorSelect", Map.of("type", displayType)))
                .setComponents(rows)
                .setAttachments()
                .queue();
    }

    public static void handleReset(ButtonInteractionEvent event, String type) {
        Member member = event.getMember();
        boolean isName = "NAME".equalsIgnoreCase(type);

        if (isName) {
            resetNameColor(member);
        } else {
            setter.setCardColor(member.getId(), "#94c6f3");
        }

        logger.info("Resetting {} color for {}", type.toLowerCase(), member.getEffectiveName());

        String key = KEY + "type.";
        String displayType = isName ? getLang(key + "name") : getLang(key + "stats");

        event.editMessage(format("color.successReset", Map.of("type", displayType)))
                .setEmbeds()
                .setAttachments()
                .setComponents()
                .queue();
    }

    public static void handleSelectMenu(StringSelectInteractionEvent event, String subAction, String typeStr) {
        boolean isName = "NAME".equalsIgnoreCase(typeStr);
        String rawValue = event.getValues().get(0);
        String color = rawValue.replace(typeStr, "");

        if (isName) {
            setNameColor(event, color);
        } else {
            sendPreview(event, color);

            // Note: Inside your StatsCmd.sendPreview method, when you attach the
            // "Confirm" button, make sure its custom ID is built using:
            // ColorCmd.buildId("confirm", color) -> outputs "color:confirm:colorValue"
        }
    }

    public static void applyStatsColor(ButtonInteractionEvent event, String color) {
        Member member = event.getMember();
        setter.setCardColor(member.getId(), color);

        logger.info("Setting stats color for {} to {}", member.getEffectiveName(), color);

        String translatedColor = colors.containsKey(color) ? colors.get(color).translation : color;

        event.editMessage(format("color.success", Map.of(
                        "type", getLang("color.type.stats"),
                        "color", translatedColor
                )))
                .setAttachments()
                .setComponents()
                .queue();
    }

    private static void setNameColor(StringSelectInteractionEvent event, String color) {
        Member member = event.getMember();
        String newRole = colors.get(color).translation;

        resetNameColor(member);

        event.getGuild().addRoleToMember(member, event.getGuild().getRolesByName(newRole, true).get(0)).queue();

        logger.info("Setting name color for {} to {}", member.getUser().getName(), color);
        event.editMessage(format("color.success", Map.of("type", getLang("color.type.name"), "color", newRole)))
                .setEmbeds()
                .setComponents()
                .queue();
    }

    private static void resetNameColor(Member member) {
        List<String> translations = getTranslations();

        member.getRoles().stream()
                .filter(role -> translations.contains(role.getName()))
                .forEach(role -> GUILD.removeRoleFromMember(member, role).queue());
    }
}