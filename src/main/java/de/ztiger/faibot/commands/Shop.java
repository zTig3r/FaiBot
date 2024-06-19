package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.Colors.*;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

@SuppressWarnings("ConstantConditions")
public class Shop {

    private static final String KEY = "shop.";

    private static final Button cancel = Button.danger("BUYcancel", getLang(KEY + "cancel"));
    private static final Button confirm = Button.success("BUYconfirm", getLang(KEY + "confirm"));
    private static final HashMap<Member, String> shopCache = new HashMap<>();

    public static void sendShopEmbed(SlashCommandInteractionEvent event) {
        event.replyEmbeds(getShopEmbed()).setComponents(getActionRows(event.getMember().getId())).setEphemeral(true).queue();
    }

    public static void sendShopEmbed(ButtonInteractionEvent event) {
        event.editMessageEmbeds(getShopEmbed()).setComponents(getActionRows(event.getMember().getId())).queue();
    }

    public static List<ActionRow> getActionRows(String id) {
        return createColorActionRows("BUY", getter.getInventory(id));
    }

    private static MessageEmbed getShopEmbed() {
        return getEmbed("shop", Map.of("price", getColorPrice() + ""));
    }

    public static void handleShopEmbed(ButtonInteractionEvent event) {
        shopCache.put(event.getMember(), event.getButton().getId().substring(3));

        event.editMessageEmbeds(getEmbed("shopConfirm", Map.of("color", colors.get(shopCache.get(event.getMember())).translation, "price", getColorPrice() + ""))).setActionRow(confirm, cancel).queue();
    }

    public static void handleBuy(ButtonInteractionEvent event) {
        int colorPrice = getColorPrice();

        if (getter.getPoints(event.getMember().getId()) < colorPrice) {
            event.reply(getLang(KEY + "error")).setEphemeral(true).queue();
            return;
        }

        String color = shopCache.get(event.getMember());

        setter.removePoints(event.getMember().getId(), colorPrice);
        setter.addInventory(event.getMember().getId(), color);

        shopCache.remove(event.getMember());

        logger.info("User {} bought the color {}", event.getMember().getUser().getEffectiveName(), color);
        event.editMessage(format(KEY + "success", Map.of("color", colors.get(color).translation))).setComponents().setEmbeds().queue();
    }

    private static int getColorPrice() {
        return cfgm.getConfig("config").getInt("colorPrice");
    }
}
