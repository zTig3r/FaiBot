package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

import static de.ztiger.faibot.FaiBot.getter;
import static de.ztiger.faibot.utils.Colors.colors;
import static de.ztiger.faibot.utils.Colors.nixo;
import static de.ztiger.faibot.utils.Lang.getLang;

@SuppressWarnings("ConstantConditions")
public class Inventory {

    private static final String KEY = "inventory.";

    public static void sendInventory(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl())
                .setTitle(getLang(KEY + "title"))
                .setColor(nixo);

        List<String> memberColors = new ArrayList<>(getter.getInventory(event.getMember().getId()));

        if (memberColors.isEmpty()) embed.addField("\u00A0", getLang(KEY + "noItems"), false);
        else memberColors.forEach(color -> embed.addField("\u00A0", colors.get(color).translation, false));

        embed.setFooter(getLang(KEY + "shop"));

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
