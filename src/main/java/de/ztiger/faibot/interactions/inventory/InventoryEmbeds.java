package de.ztiger.faibot.interactions.inventory;

import de.ztiger.faibot.localization.keys.Inventory;
import de.ztiger.faibot.utils.BotEmbed;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

public class InventoryEmbeds {

    public static MessageEmbed inventoryEmbed(List<String> items) {
        BotEmbed embed = BotEmbed.defaultEmbed()
                .title(Localization.get(Inventory.Embed.TITLE))
                .description(Localization.get(Inventory.Embed.DESCRIPTION))
                .emptyLine();

        if (items.isEmpty()) {
            embed.normalField(Localization.get(Inventory.Embed.NO_ITEMS));
        } else {
            items.forEach(embed::normalField);
        }

        return embed
                .emptyLine()
                .boldField(Localization.get(Inventory.Embed.BUY_MORE))
                .build();
    }
}
