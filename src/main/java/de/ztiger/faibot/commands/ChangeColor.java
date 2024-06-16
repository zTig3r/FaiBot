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

    private static final Button reset = Button.danger("reset", getLang(KEY + ".reset"));
    private static final Button back = Button.danger("back", "↩️ Zurück");
    private static final Button nameColor = Button.primary("nameColor", "👋 Name");
    private static final Button statsColor = Button.primary("statsColor", "📊 Statistiken");

    public static void sendColorEmbed(SlashCommandInteractionEvent event) {
        event.replyEmbeds(getEmbed("changeColorMenu")).addActionRow(nameColor, statsColor).setEphemeral(true).queue();
    }

    public static void sendColorEmbed(ButtonInteractionEvent event) {
        event.editMessageEmbeds(getEmbed("changeColorMenu")).setActionRow(nameColor, statsColor).queue();
    }

    public static void colorEmbed(ButtonInteractionEvent event, boolean isName) {
        List<ActionRow> rows = createColorActionRows();
        List<ItemComponent> lastRowComponents = rows.get(rows.size() - 1).getComponents();

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

    public static void changeColor(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();

        if (buttonId.equals("reset")) handleReset(event);
        else if (getter.getInventory(event.getMember().getId()).contains(buttonId)) handleColor(event);
        else event.reply(getLang("color.locked")).setEphemeral(true).queue();
    }

    private static void handleReset(ButtonInteractionEvent event) {
        String title = event.getMessage().getEmbeds().get(0).getTitle();
        Member member = event.getMember();

        if (title.contains("Name")) {
            resetNameColor(member);
            logger.info("Resetting namecolor for {}", member.getEffectiveName());
        } else if (title.contains("Statistiken")) {
            setter.setCardColor(member.getId(), "#94c6f3");
            logger.info("Resetting statscolor for {}", member.getEffectiveName());
        }

        String key = "color.type.";

        event.editMessage(format("color.successReset", Map.of("type", title.contains("Name") ? getLang(key + "name") : getLang(key + "stats")))).setEmbeds().setAttachments().setComponents().queue();
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

        logger.info("Setting statscolor for {} to {}", member.getEffectiveName(), colorCache.get(member));
        event.editMessage(format("color.success", Map.of("color", colors.get(colorCache.get(member)).translation))).setAttachments().setComponents().queue();

        colorCache.remove(member);
    }

    private static void setNameColor(ButtonInteractionEvent event, String color) {
        Member member = event.getMember();
        String newRole = colors.get(color).translation;

        resetNameColor(member);

        event.getGuild().addRoleToMember(member, event.getGuild().getRolesByName(newRole, true).get(0)).queue();

        logger.info("Setting namecolor for {} to {}", member.getUser().getName(), color);
        event.editMessage(format("color.success", Map.of("color", newRole))).setEmbeds().setComponents().queue();
    }

    private static void resetNameColor(Member member) {
        List<String> translations = getTranslations();

        member.getRoles().stream()
                .filter(role -> translations.contains(role.getName()))
                .forEach(role -> GUILD.removeRoleFromMember(member, role).queue());
    }
}
