package de.ztiger.faibot.interactions.shop;

import de.ztiger.faibot.localization.keys.Shop;
import de.ztiger.faibot.utils.BotEmbed;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ShopEmbeds {

    public static MessageEmbed shopEmbed(int price) {
        return BotEmbed.defaultEmbed()
                .title(Localization.get(Shop.Embed.Menu.TITLE))
                .description(Localization.get(Shop.Embed.Menu.DESCRIPTION))
                .emptyLine()
                .boldField(Localization.format(Shop.Embed.Menu.PRICING, "price", price))
                .emptyLine()
                .boldField(Localization.get(Shop.Embed.Menu.NEXT_STEP))
                .build();
    }

    public static MessageEmbed confirmPurchase(String color, int price) {
        return BotEmbed.defaultEmbed()
                .title(Localization.get(Shop.Embed.Confirm.TITLE))
                .description(Localization.format(Shop.Embed.Confirm.DESCRIPTION, "color", color, "price", price))
                .build();
    }
}
