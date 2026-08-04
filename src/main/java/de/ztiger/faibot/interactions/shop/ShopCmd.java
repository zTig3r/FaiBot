package de.ztiger.faibot.interactions.shop;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IButtonHandler;
import de.ztiger.faibot.localization.keys.Shop;
import de.ztiger.faibot.utils.Localization;
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

import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.config.ConfigHelper.getColorPrice;

public class ShopCmd implements ICommand, IButtonHandler {

    private static final String PREFIX = "shop";

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

        if (outcomeMessage.equals(Localization.get(Shop.ERROR))) {
            event.reply(outcomeMessage).setEphemeral(true).queue();
        } else {
            event.editMessage(outcomeMessage).setComponents().setEmbeds().queue();
        }
    }

    public void handleShopSelection(StringSelectInteractionEvent event) {
        String selectedColor = event.getValues().getFirst();

        Button confirmBtn = Button.success(buildId(ACTION_CONFIRM, selectedColor), Localization.get(Shop.CONFIRM));
        Button cancelBtn = Button.danger(buildId(ACTION_CANCEL, null), Localization.get(Shop.CANCEL));

        event.editMessageEmbeds(ShopEmbeds.confirmPurchase(selectedColor, getColorPrice())).setComponents(ActionRow.of(confirmBtn, cancelBtn)).queue();
    }

    public static void sendShopEmbed(ButtonInteractionEvent event) {
        event.editMessageEmbeds(getShopEmbed()).setComponents(getActionRows(event.getMember().getId())).queue();
    }

    public static List<ActionRow> getActionRows(String id) {
        return new ArrayList<>();
    }

    private static MessageEmbed getShopEmbed() {
        return ShopEmbeds.shopEmbed(getColorPrice());
    }
}