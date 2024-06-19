package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.commands.Stats.sendPreview;
import static de.ztiger.faibot.listeners.BotReady.GUILD;
import static de.ztiger.faibot.utils.Colors.*;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

@SuppressWarnings("ConstantConditions")
public class ChangeColor {

    private static final String KEY = "color.";
    private static final HashMap<Member, String> colorCache = new HashMap<>();

    private static final Button back = Button.danger("back", getLang(KEY + "back"));
    private static final Button nameColor = Button.primary("nameColor", getLang(KEY + "name"));
    private static final Button statsColor = Button.primary("statsColor", getLang(KEY + "stats"));

    public static void sendColorEmbed(SlashCommandInteractionEvent event) {
        event.replyEmbeds(getEmbed("changeColorMenu")).addActionRow(nameColor, statsColor).setEphemeral(true).queue();
    }

    public static void sendColorEmbed(ButtonInteractionEvent event) {
        event.editMessageEmbeds(getEmbed("changeColorMenu")).setActionRow(nameColor, statsColor).queue();
    }

    public static void colorEmbed(ButtonInteractionEvent event, boolean isName) {
        String type = isName ? "NAME" : "STATS";

        List<ActionRow> rows = createColorActionRows(type);
        List<ItemComponent> lastRowComponents = rows.get(rows.size() - 1).getComponents();

        Button reset = Button.danger(type + "reset", getLang(KEY + "reset"));

        switch (lastRowComponents.size()) {
            case 1, 2, 3 -> {
                lastRowComponents.add(reset);
                lastRowComponents.add(back);
            }
            case 4 -> {
                lastRowComponents.add(reset);
                rows.add(ActionRow.of(back));
            }
            case 5 -> rows.add(ActionRow.of(reset, back));
        }

        event.editMessageEmbeds(getEmbed("changeColorSelect", Map.of("type", isName ? getLang("color.type.name") : getLang("color.type.stats")))).setComponents(rows).setAttachments().queue();
    }

    public static void handleReset(ButtonInteractionEvent event, boolean isName) {
        Member member = event.getMember();

        if (isName) resetNameColor(member);
        else setter.setCardColor(member.getId(), "#94c6f3");

        logger.info("Resetting {} color for {}", isName ? "name" : "stats", member.getEffectiveName());

        String key = KEY + "type.";

        event.editMessage(format("color.successReset", Map.of("type", isName ? getLang(key + "name") : getLang(key + "stats")))).setEmbeds().setAttachments().setComponents().queue();
    }

    public static void handleColor(ButtonInteractionEvent event, boolean isName) {
        String color = event.getButton().getId().replace(isName ? "NAME" : "STATS", "");

        if (!getter.getInventory(event.getMember().getId()).contains(color)) {
            event.reply(getLang("color.locked")).setEphemeral(true).queue();
            return;
        }

        if (isName) setNameColor(event, color);
        else {
            colorCache.put(event.getMember(), color);
            sendPreview(event, color);
        }
    }

    public static void applyStatsColor(ButtonInteractionEvent event) {
        Member member = event.getMember();
        setter.setCardColor(member.getId(), colorCache.get(member));

        logger.info("Setting stats color for {} to {}", member.getEffectiveName(), colorCache.get(member));
        event.editMessage(format("color.success", Map.of("type", getLang("color.type.stats"), "color", colors.get(colorCache.get(member)).translation))).setAttachments().setComponents().queue();

        colorCache.remove(member);
    }

    private static void setNameColor(ButtonInteractionEvent event, String color) {
        Member member = event.getMember();
        String newRole = colors.get(color).translation;

        resetNameColor(member);

        event.getGuild().addRoleToMember(member, event.getGuild().getRolesByName(newRole, true).get(0)).queue();

        logger.info("Setting name color for {} to {}", member.getUser().getName(), color);
        event.editMessage(format("color.success", Map.of("type", getLang("color.type.name"), "color", newRole))).setEmbeds().setComponents().queue();
    }

    private static void resetNameColor(Member member) {
        List<String> translations = getTranslations();

        member.getRoles().stream()
                .filter(role -> translations.contains(role.getName()))
                .forEach(role -> GUILD.removeRoleFromMember(member, role).queue());
    }
}
