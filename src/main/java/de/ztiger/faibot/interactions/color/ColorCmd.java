package de.ztiger.faibot.interactions.color;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IButtonHandler;
import de.ztiger.faibot.interactions.components.ISelectHandler;
import de.ztiger.faibot.localization.keys.Color;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.interactions.stats.StatsHelper.createStatsImage;
import static de.ztiger.faibot.utils.Colors.*;

public class ColorCmd implements ICommand, IButtonHandler, ISelectHandler {

    private static final String PREFIX = "color";
    private static final String KEY = "color.";

    private static final String ACTION_BACK = "back";
    private static final String ACTION_MENU_NAME = "menu_name";
    private static final String ACTION_MENU_STATS = "menu_stats";
    private static final String ACTION_RESET = "reset";
    private static final String ACTION_CONFIRM = "confirm";

    private final ColorService colorService = new ColorService();

    @Override
    public CommandData getCommandData() {
        return Commands.slash("color", "Ändere deine Farben");
    }

    @Override
    public String getComponentPrefix() {
        return PREFIX;
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        event.replyComponents(getMainMenuButtons()).setEmbeds(ColorEmbeds.changeColorMenu()).setEphemeral(true).queue();
    }

    @Override
    public void handleButton(ButtonInteractionEvent event, String action, String payload) {
        switch (action) {
            case ACTION_BACK -> sendMainColorMenu(event);
            case ACTION_MENU_NAME -> sendSubMenuEmbed(event, true);
            case ACTION_MENU_STATS -> sendSubMenuEmbed(event, false);
            case ACTION_RESET -> handleReset(event, payload);
            case ACTION_CONFIRM -> applyStatsColor(event, payload);
            default -> logger.warn("Unknown color button action: {}", action);
        }
    }

    @Override
    public void executeSelect(StringSelectInteractionEvent event) {
        String[] parts = event.getComponentId().split(":");
        if (parts.length < 3) return;

        String subAction = parts[1];
        String typeStr = parts[2];

        if ("select".equalsIgnoreCase(subAction)) {
            handleSelectMenu(event, subAction, typeStr);
        }
    }

    private void sendMainColorMenu(ButtonInteractionEvent event) {
        event.editComponents(getMainMenuButtons()).setEmbeds(ColorEmbeds.changeColorMenu()).queue();
    }

    public void sendSubMenuEmbed(ButtonInteractionEvent event, boolean isName) {
        String typeStr = isName ? "NAME" : "STATS";

        Button reset = Button.danger(buildId(ACTION_RESET, typeStr), Localization.get(Color.RESET));
        Button back = Button.secondary(buildId(ACTION_BACK), Localization.get(Color.BACK));

        String selectMenuId = PREFIX + ":select:" + typeStr;
        StringSelectMenu menu = StringSelectMenu.create(selectMenuId)
                .addOptions(getColorOptions(typeStr, getter.getInventory(event.getMember().getId())))
                .build();

        String displayType = Localization.get(isName ? Color.Type.NAME : Color.Type.STATS);

        event.editMessageEmbeds(ColorEmbeds.changeColorSelect(displayType))
                .setComponents(ActionRow.of(menu), ActionRow.of(reset, back))
                .setAttachments()
                .queue();
    }

    public void handleReset(ButtonInteractionEvent event, String type) {
        Member member = event.getMember();
        boolean isName = "NAME".equalsIgnoreCase(type);

        if (member == null) return;

        if (isName) {
            colorService.removeCurrentNameColors(member);
        } else {
            colorService.resetStatsColor(member.getId());
        }

        logger.info("Resetting {} color for {}", type.toLowerCase(), member.getEffectiveName());

        String displayType = Localization.get(isName ? Color.Type.NAME : Color.Type.STATS);

        event.editMessage(Localization.format("color.successReset", "type", displayType))
                .setEmbeds().setAttachments().setComponents().queue();
    }

    public void handleSelectMenu(StringSelectInteractionEvent event, String subAction, String typeStr) {
        boolean isName = "NAME".equalsIgnoreCase(typeStr);
        String rawValue = event.getValues().getFirst();
        String color = rawValue.replace(typeStr, "");

        if (isName) {
            colorService.applyNameColor(event.getMember(), color);

            String newRole = colors.get(color).translation;
            logger.info("Setting name color for {} to {}", event.getUser().getName(), color);

            event.editMessage(Localization.format("color.success", "type", Localization.get("color.type.name"), "color", newRole))
                    .setEmbeds().setComponents().queue();
        } else {
            Button confirm = Button.success(buildId(ACTION_CONFIRM, color), Localization.get("stats.apply"));
            Button back = Button.danger(buildId(ACTION_MENU_STATS), Localization.get("stats.cancel"));

            event.editMessage("").setAttachments(FileUpload.fromData(createStatsImage(event.getMember(), convertColor(color)))).setComponents(ActionRow.of(confirm, back)).setEmbeds().queue();
        }
    }

    public void applyStatsColor(ButtonInteractionEvent event, String color) {
        Member member = event.getMember();

        colorService.updateStatsColor(member.getId(), color);

        logger.info("Setting stats color for {} to {}", member.getEffectiveName(), color);

        String translatedColor = colors.containsKey(color) ? colors.get(color).translation : color;

        event.editMessage(Localization.format("color.success", Map.of("type", Localization.get("color.type.stats"), "color", translatedColor)))
                .setAttachments().setComponents().queue();
    }

    private ActionRow getMainMenuButtons() {
        return ActionRow.of(
                Button.primary(buildId(ACTION_MENU_NAME), Localization.get(Color.NAME)),
                Button.primary(buildId(ACTION_MENU_STATS), Localization.get(Color.STATS))
        );
    }
}