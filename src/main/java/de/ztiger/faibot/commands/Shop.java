package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.Colors.*;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

public class Shop implements ICommand {

    private static final String KEY = "shop.";

    private static final Button cancel = Button.danger("BUYcancel", getLang(KEY + "cancel"));
    private static final Button confirm = Button.success("BUYconfirm", getLang(KEY + "confirm"));
    private static final HashMap<Member, String> shopCache = new HashMap<>();

    @Override
    public CommandData getCommandData() {
        return Commands.slash("shop", "Zeigt den Shop an");
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        event.replyEmbeds(getShopEmbed()).setComponents(getActionRows(event.getMember().getId())).setEphemeral(true).queue();
    }

    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String id = event.getButton().getCustomId();

        if (id.equals("shop:buy_cancel")) {
            sendShopEmbed(event);
        } else if (id.equals("shop:buy_confirm")) {
            handleBuy(event);
        }
    }

    public static void sendShopEmbed(ButtonInteractionEvent event) {
        event.editMessageEmbeds(getShopEmbed()).setComponents(getActionRows(event.getMember().getId())).queue();
    }

    public static List<ActionRow> getActionRows(String id) {
        //return getColorOptions("BUY", getter.getInventory(id));
        return new ArrayList<>();
    }

    private static MessageEmbed getShopEmbed() {
        return getEmbed("shop", Map.of("price", getColorPrice() + ""));
    }

    public static void handleShopEmbed(StringSelectInteractionEvent event) {
        //  shopCache.put(event.getMember(), event.getButton().getCustomId().substring(3));

        //event.editMessageEmbeds(getEmbed("shopConfirm", Map.of("color", colors.get(shopCache.get(event.getMember())).translation, "price", getColorPrice() + ""))).setActionRow(confirm, cancel).queue();
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
