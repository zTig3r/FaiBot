package de.ztiger.faibot.interactions.shop;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IButtonHandler;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.config.ConfigHelper.getColorPrice;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.getLang;

public class ShopCmd implements ICommand, IButtonHandler {

    private static final String PREFIX = "shop";
    private static final String KEY = "shop.";

    private static final String ACTION_CONFIRM = "confirm";
    private static final String ACTION_CANCEL = "cancel";

    @Override
    public CommandData getCommandData() {
        return Commands.slash("shop", "Zeigt den Shop an");
    }

    @Override
    public String getComponentPrefix() {
        return PREFIX;
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        event.replyEmbeds(getShopEmbed()).setComponents(getActionRows(event.getMember().getId())).setEphemeral(true).queue();
    }

    @Override
    public void handleButton(ButtonInteractionEvent event, String action, String payload) {
        switch (action) {
            case ACTION_CONFIRM -> handleBuy(event, payload);
            case ACTION_CANCEL -> sendShopEmbed(event);
            default -> logger.warn("Unknown shop action: {}", action);
        }
    }

    public void handleBuy(ButtonInteractionEvent event, String color) {
        int price = getColorPrice();

        String outcomeMessage = ShopService.processColorPurchase(event.getMember(), color, price);

        if (outcomeMessage.equals(getLang(KEY + "error"))) {
            event.reply(outcomeMessage).setEphemeral(true).queue();
        } else {
            event.editMessage(outcomeMessage).setComponents().setEmbeds().queue();
        }
    }

    public void handleShopSelection(StringSelectInteractionEvent event) {
        String selectedColor = event.getValues().get(0);

        Button confirmBtn = Button.success(buildId(ACTION_CONFIRM, selectedColor), getLang("shop.confirm"));
        Button cancelBtn = Button.danger(buildId(ACTION_CANCEL, null), getLang("shop.cancel"));

        event.editMessageEmbeds(getEmbed("shopConfirm", Map.of("color", selectedColor, "price",
                getColorPrice() + ""))).setComponents(ActionRow.of(confirmBtn, cancelBtn)).queue();
    }

    public static void sendShopEmbed(ButtonInteractionEvent event) {
        event.editMessageEmbeds(getShopEmbed()).setComponents(getActionRows(event.getMember().getId())).queue();
    }

    public static List<ActionRow> getActionRows(String id) {
        return new ArrayList<>();
    }

    private static MessageEmbed getShopEmbed() {
        return getEmbed("shop", Map.of("price", getColorPrice() + ""));
    }
}