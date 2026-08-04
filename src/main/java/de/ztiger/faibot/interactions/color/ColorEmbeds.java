package de.ztiger.faibot.interactions.color;

import de.ztiger.faibot.localization.keys.Color;
import de.ztiger.faibot.utils.BotEmbed;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ColorEmbeds {

    public static MessageEmbed changeColorMenu() {
        return BotEmbed.defaultEmbed()
                .title(Localization.get(Color.Embed.ChangeColor.TITLE))
                .description(Localization.get(Color.Embed.ChangeColor.DESCRIPTION))
                .emptyLine()
                .boldField(Localization.get(Color.Embed.ChangeColor.SELECT))
                .build();
    }

    public static MessageEmbed changeColorSelect(String type) {
        return BotEmbed.defaultEmbed()
                .title(Localization.format(Color.Embed.ChangeColorSelect.TITLE, "type", type))
                .description(Localization.format(Color.Embed.ChangeColorSelect.DESCRIPTION, "type", type))
                .emptyLine()
                .boldField(Localization.get(Color.Embed.ChangeColorSelect.WARNING))
                .emptyLine()
                .boldField(Localization.format(Color.Embed.ChangeColorSelect.SELECT, "type", type))
                .build();
    }
}
